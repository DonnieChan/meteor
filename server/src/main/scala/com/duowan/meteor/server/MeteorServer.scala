package com.duowan.meteor.server

import java.net.URLDecoder
import java.util.ArrayList
import java.util.Calendar
import java.util.UUID
import scala.annotation.elidable
import scala.annotation.elidable.ASSERTION
import scala.annotation.migration
import scala.collection.JavaConversions.asScalaSet
import scala.collection.JavaConversions.iterableAsScalaIterable
import scala.collection.JavaConversions.mapAsJavaMap
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions.mutableMapAsJavaMap
import scala.collection.JavaConverters
import scala.collection.mutable
import scala.reflect.runtime.universe
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang3.time.DateFormatUtils
import org.apache.commons.lang3.time.DateUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Time
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Assign
import org.apache.spark.streaming.kafka010.HasOffsetRanges
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import com.duowan.meteor.model.view.importqueue.ImportKafkaTask
import com.duowan.meteor.server.checkpoint.ZkOffsetCheckPoint
import com.duowan.meteor.server.context.CassandraContextSingleton
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.cron.CronTaskLoader
import com.duowan.meteor.server.executor.instance.InstanceFlowExecutor
import com.duowan.meteor.server.factory.DefTaskFactory
import com.duowan.meteor.server.factory.InstanceFlowExecutorObjectPool
import com.duowan.meteor.server.factory.TaskThreadPoolFactory
import com.duowan.meteor.server.util.LocalCacheUtil
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.MiscCacheUtil
import com.duowan.meteor.server.util.RedisClusterUtil
import com.duowan.meteor.server.util.KafkaOffsetUtil
import org.apache.spark.storage.StorageLevel
import org.apache.commons.codec.digest.DigestUtils
import com.duowan.meteor.server.util.LocalCacheUtil2

object MeteorServer extends Logging {

  def main(args: Array[String]) {
    logInfo("Starting MeteorServer!")

    val spark = SparkSession.builder.appName(ExecutorContext.appName).getOrCreate()
    ExecutorContext.spark = spark

    val streamContext = new StreamingContext(spark.sparkContext, Seconds(ExecutorContext.patchSecond))
    ExecutorContext.streamContext = streamContext

    val offsetCheckpointListener = new ZkOffsetCheckPoint
    offsetCheckpointListener.registerCheckpointListener(streamContext)

    DefTaskFactory.startup()
    CronTaskLoader.startup()

    regUDF(spark)
    importQueue()

    streamContext.start()
    logInfo("Finished startup")
    streamContext.awaitTermination()
  }

  def importQueue(): Unit = {
    for (sourceTaskId: Integer <- DefTaskFactory.defAllValid.getImportQueueSet.toSet) {
      val task = DefTaskFactory.getCloneById(sourceTaskId).asInstanceOf[ImportKafkaTask]

      if (!StringUtils.equals(System.getenv("DWENV"), "prod")) {
        task.setBrokers(ExecutorContext.kafkaClusterHostPorts)
      }
      logInfo(s"Startup sourceTask : ${task.getFileId}, ${task.getFileName}, ${task.getTopics}")
      val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> task.getBrokers,
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> task.getGroupId,
        "max.poll.records" -> ExecutorContext.maxPollRecord,
        "max.partition.fetch.bytes" -> ExecutorContext.maxPartitionFetchBytes,
        "receive.buffer.bytes" -> (1048576: java.lang.Integer),
        "session.timeout.ms" -> (120000: java.lang.Integer),
        "heartbeat.interval.ms" -> (15000: java.lang.Integer),
        "enable.auto.commit" -> (false: java.lang.Boolean))

      val topicSet = scala.collection.mutable.Set[String]()
      for (topic: String <- StringUtils.split(task.getTopics, ",")) {
        topicSet += StringUtils.trim(topic)
      }
//      ExecutorContext.streamContext.sparkContext.setLocalProperty("spark.scheduler.pool", task.getPriority.toString())

      modifyCheckpointOffsets(task, topicSet)

      var topicAndPartitionMap = scala.collection.immutable.Map[TopicPartition, Long]()
      for ((topicParStr, offset) <- ExecutorContext.topicAndPartitions) {
        val topicParArray = topicParStr.split(":")
        if (topicSet.contains(topicParArray(0))) {
          topicAndPartitionMap += new TopicPartition(topicParArray(0), topicParArray(1).toInt) -> offset.toLong
        }
      }

      var streamRe = KafkaUtils.createDirectStream[String, String](
        ExecutorContext.streamContext,
        PreferConsistent,
        Assign[String, String](topicAndPartitionMap.keys.toList, kafkaParams, topicAndPartitionMap.toMap))

      streamRe.foreachRDD((rdd: RDD[ConsumerRecord[String, String]], time: Time) => {
        TaskThreadPoolFactory.cachedThreadPool.submit(new Runnable() {
          override def run(): Unit = {
            
            val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges.sortBy(x => x.partition)
            var str = "\n"
            for (o <- offsetRanges) {
              str += s"${o.topic}    ${o.partition}    ${o.fromOffset}    ${o.untilOffset}  ${o.untilOffset - o.fromOffset}\n"
              ExecutorContext.topicAndPartitions.put(s"${o.topic}:${o.partition}", o.untilOffset.toString)
            }
            logInfo(str)
            
            if (!rdd.isEmpty()) {
              val instanceFlowExecutor = InstanceFlowExecutorObjectPool.getPool(sourceTaskId).borrowObject().asInstanceOf[InstanceFlowExecutor]
              InstanceFlowExecutorObjectPool.getPool(sourceTaskId).invalidateObject(instanceFlowExecutor)
              instanceFlowExecutor.time = System.currentTimeMillis()
              val paramMap = Map[String, Any]("rdd" -> rdd.map(_.value()))
              instanceFlowExecutor.startup(sourceTaskId, paramMap)
            }
          }
        })
      })
    }
  }

  //修改offset，例如offset越界或者partition增加减少等，都有必要修改
  //offset越界的时候，直接去最大的offset
  def modifyCheckpointOffsets(task: ImportKafkaTask, topics: mutable.Set[String]): Unit = {
    val earliestOffsets = KafkaOffsetUtil.getEarliestOffset(task.getBrokers, topics, task.getGroupId + "GetOffset")
    val latestOffsets = KafkaOffsetUtil.getLastOffset(task.getBrokers, topics, task.getGroupId + "GetOffset")
    
    val earliestOffsetSet = mutable.Map[String, Long]()
    for ((topicAndPartition, offset) <- earliestOffsets) {
      earliestOffsetSet += topicAndPartition.topic + ":" + topicAndPartition.partition -> offset
    }
    val latestOffsetSet = mutable.Map[String, Long]()
    for ((topicAndPartition, offset) <- latestOffsets) {
      latestOffsetSet += topicAndPartition.topic + ":" + topicAndPartition.partition -> offset
    }
    assert(latestOffsetSet.size == earliestOffsetSet.size)

    for ((topicAndPartition, offset) <- latestOffsetSet) {
      if (ExecutorContext.topicAndPartitions.containsKey(topicAndPartition)) {
        val parOffset = ExecutorContext.topicAndPartitions.get(topicAndPartition).toLong
        if (parOffset > offset || parOffset < earliestOffsetSet.get(topicAndPartition).get) {
          ExecutorContext.topicAndPartitions += topicAndPartition -> latestOffsetSet.get(topicAndPartition).get.toString
          logInfo(s"the checkpoint offset[${parOffset}}] of [${topicAndPartition}] out of range ${earliestOffsetSet.get(topicAndPartition).get}--${offset} " +
            s"use max offset replace")
        } else {
          logInfo(s"the checkpoint offset of [${topicAndPartition}] is [${parOffset}}]!")
        }
      } else {
        ExecutorContext.topicAndPartitions += topicAndPartition -> offset.toString
        logInfo(s"[${topicAndPartition}] has not checkpoint offset,use max offset[${offset.toString}] replace")
      }
    }

  }

  //$SPARK_HOME/con/spark-default
  //spark.driver.extraClassPath  /home/spark/spark/lib_ext/*
  //spark.executor.extraClassPath  /home/spark/spark/lib_ext/*
  def regUDF(spark: SparkSession): Unit = {
    logInfo("Startup regUDF")

    spark.udf.register("c_uuid", () => {
      UUID.randomUUID().toString()
    })

    spark.udf.register("json_2map", (jsonStr: String) => {
      var result = Map[String, String]()
      try {
        if (StringUtils.isNotBlank(jsonStr)) {
          val resultMap = com.alibaba.fastjson.JSON.parseObject(jsonStr, new com.alibaba.fastjson.TypeReference[java.util.Map[String, String]]() {})
          result = resultMap.toMap
        }
      } catch {
        case e: Exception => {}
      }
      result
    })

    spark.udf.register("array_json_2map", (jsonArrStr: String) => {
      var result = Array[Map[String, String]]()
      try {
        val typeRef = new com.alibaba.fastjson.TypeReference[java.util.ArrayList[java.util.HashMap[String, String]]]() {}
        val mapArr = com.alibaba.fastjson.JSON.parseObject(jsonArrStr, typeRef);
        result = mapArr.toArray().map { x => x.asInstanceOf[java.util.HashMap[String, String]].toMap }
      } catch {
        case e: Exception => {}
      }
      result
    })

    spark.udf.register("map_2str", (map: Map[String, String]) => {
      if (map != null) {
        new com.alibaba.fastjson.JSONObject(map).toString()
      } else {
        "{}"
      }
    })

    spark.udf.register("merge_2map", (map1: Map[String, String], map2: Map[String, String]) => {
      var result = scala.collection.mutable.Map[String, String]();
      if (map1 != null) {
        result.putAll(map1)
      }
      if (map2 != null) {
        result.putAll(map2)
      }
      result
    })

    spark.udf.register("struct_2map", (data: GenericRowWithSchema) => {
      var result = scala.collection.mutable.Map[String, String]();
      if (data != null) {
        val fieldNames = data.schema.fieldNames
        var i = 0
        for (fieldName <- fieldNames) {
          val d = data.get(i)
          if (d != null) {
            result += fieldName -> d.toString
          }
          i += 1
        }
      }
      result
    })

    spark.udf.register("c_join_v2", (table: String, toCassandra: Boolean, useLocalCache: Boolean, useRedis: Boolean, cacheEmpty: Boolean, cassandraExpireSeconds: Integer, redisExpireSeconds: Integer, partition: String, key: String) => {
      var result = "{}"
      if (StringUtils.isNotBlank(key)) {
        var parKey = key
        if (StringUtils.isNotBlank(partition)) {
          parKey = partition + "|" + key
        }

        val dataKey = table + "|" + parKey
        var continueFlag = true
        if (useLocalCache) {
          val localCacheResult = LocalCacheUtil.get(dataKey)
          if (StringUtils.isNotBlank(localCacheResult)) {
            result = localCacheResult
            continueFlag = false
          }
        }

        if (continueFlag && useRedis) {
          val redisResult = RedisClusterUtil.get(dataKey)
          if (StringUtils.isNotBlank(redisResult)) {
            result = redisResult
            continueFlag = false
            if (useLocalCache) LocalCacheUtil.put(dataKey, redisResult)
          } else if (!toCassandra && useLocalCache && cacheEmpty) {
            LocalCacheUtil.put(dataKey, "{}")
          }
        }

        if (continueFlag && toCassandra) {
          val session = CassandraContextSingleton.getSession()
          val tablePS = CassandraContextSingleton.getPreparedStatement(s"SELECT value FROM $table WHERE key=?", table, cassandraExpireSeconds)
          val tableRS = session.execute(tablePS.bind(parKey))
          var isNotExists = true
          if (tableRS != null) {
            val tableRSList = tableRS.toList
            if (tableRSList != null && tableRSList.size > 0) {
              val jsonData = tableRSList(0).getString("value")
              if (StringUtils.isNotBlank(jsonData)) {
                isNotExists = false
                result = jsonData
                if (useLocalCache) LocalCacheUtil.put(dataKey, jsonData)
                if (useRedis) RedisClusterUtil.setex(dataKey, jsonData, redisExpireSeconds)
              }
            }
          }
          if (isNotExists && useLocalCache && cacheEmpty) {
            LocalCacheUtil.put(dataKey, "{}")
          }
        }
      }
      result
    })

    spark.udf.register("get_slot_time", (time: String, slot: Integer, sDateFormat: String, tDateFormat: String) => {
      val date = DateUtils.parseDate(time, sDateFormat)
      val calendar = Calendar.getInstance()
      calendar.setTime(date)
      calendar.set(Calendar.SECOND, 0)
      calendar.set(Calendar.MILLISECOND, 0)
      val minuteSlot = (calendar.get(Calendar.MINUTE) / slot) * slot
      calendar.set(Calendar.MINUTE, minuteSlot)
      DateFormatUtils.format(calendar, tDateFormat)
    })

    spark.udf.register("urlDecode", (url: String) => {
      var result = url
      if (StringUtils.isBlank(url)) {
        result = "OTHER"
      } else {
        val value = MiscCacheUtil.get(url)
        if (value != null) {
          result = value
        } else {
          if (StringUtils.isNotBlank(url)) {
            try {
              result = URLDecoder.decode(url, "UTF-8")
            } catch {
              case e: Exception => {}
            }
          }
          MiscCacheUtil.put(url, result)
        }
      }
      result
    })

    spark.udf.register("get_level_array", (input: String, splitStr: String, fromLevelN: Int) => {
      var result = Array[String]("OTHER")
      if (StringUtils.isNotBlank(input)) {
        val inputSplitArr = StringUtils.split(input, splitStr)
        val itemList = new java.util.ArrayList[String]()
        for (item <- inputSplitArr) {
          if (StringUtils.isNotBlank(item)) {
            itemList.add(StringUtils.trim(item))
          }
        }
        val itemListSize = itemList.size()
        if (itemListSize != 0 && itemListSize <= fromLevelN) {
          result = Array(StringUtils.join(itemList, splitStr))
        } else if (itemListSize != 0 && itemListSize > fromLevelN) {
          val resultTmp = scala.collection.mutable.ListBuffer[String]()
          var itemConcat = ""
          var i = 1
          for (item <- itemList) {
            if (i == 1) {
              itemConcat = item
            } else {
              itemConcat += "/" + item
            }
            if (i >= fromLevelN) {
              resultTmp += itemConcat
            }
            i += 1
          }
          result = resultTmp.toArray[String]
        }
      }
      result
    })

    spark.udf.register("get_day_of_week", (dateStr: String, pattern: String, dayOfWeek: Int) => {
      val tdate = DateUtils.parseDate(dateStr, pattern)
      val c = DateUtils.toCalendar(tdate)
      c.setFirstDayOfWeek(Calendar.MONDAY)
      c.set(Calendar.DAY_OF_WEEK, dayOfWeek)
      DateFormatUtils.format(c, "yyyyMMdd").toInt
    })
  }
}