package com.duowan.meteor.server.executor

import scala.collection.mutable.ListBuffer
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.MapType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType
import org.apache.spark.storage.StorageLevel
import com.duowan.meteor.model.view.importqueue.ImportKafkaTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import org.apache.kafka.clients.consumer.ConsumerRecord

class ImportKafkaTaskExecutor extends AbstractTaskExecutor {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val spark = ExecutorContext.spark
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ImportKafkaTask]
    val sourceRdd = paramMap.get("rdd").get.asInstanceOf[RDD[ConsumerRecord[String, String]]]
    instanceTaskExecutor.instanceFlowExecutor.rdd = sourceRdd

    import spark.implicits._
    sourceRdd.map(x => (x.key(), x.value())).toDF("key", "value").createOrReplaceTempView(task.getRegTable)
    spark.table(task.getRegTable).persist(StorageLevel.MEMORY_ONLY)
  }

}