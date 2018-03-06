package com.duowan.meteor.server.context

import java.util.concurrent.ConcurrentHashMap

import scala.collection.mutable.ListBuffer

import org.apache.commons.lang3.StringUtils
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.StreamingContext

import com.duowan.meteor.server.util.PropertiesUtil

object ExecutorContext {

	val propFilePath = "/data/apps/spark/conf/meteor.properties"
  
	init()
  
	var log4jPath: String = _
  var DWENV: String = _
  var appName: String = _
  var patchSecond: Int = _
  var execCronTaskOnStartup: String = _

  var kafkaClusterHostPorts: String = _
  var redisMultiHostPorts: String = _
  var redisMaxTotals: Int = _
  var redisMaxIdle: Int = _
  val redisTimeout = 30000
  var zkConnection: String = _
  val appCloseZkNamesparce: String = "appCloseListener"

  var jdbcDriver: String = _
  var jdbcUrl: String = _
  var jdbcUsername: String = _
  var jdbcPassword: String = _

  var sshExecCronTaskMachines: Array[String] = _
  var cronTaskExecJar: String = _
  var cronTaskLogPath: String = _

  var spark: SparkSession = _
  var streamContext: StreamingContext = _

  val instanceTaskTopic = "instance_task"
  val instanceFlowTopic = "instance_flow"
  val performanceTopic = "performance"

  var execSourceTaskIds: List[Int] = _
  var excludeTaskIds: ListBuffer[Int] = _

  val topicAndPartitions = new ConcurrentHashMap[String, String]()
  
  var maxPollRecord: java.lang.Integer = _
  var maxPartitionFetchBytes: java.lang.Integer = _
  var isKafkaAuth: String = _

  def init(): Unit = {
    PropertiesUtil.load(propFilePath)

    maxPollRecord = Integer.parseInt(PropertiesUtil.get("max.poll.records", "4000"))
    maxPartitionFetchBytes = Integer.parseInt(PropertiesUtil.get("max.partition.fetch.bytes", "5242880"))
    
    DWENV = PropertiesUtil.get("meteor.DWENV", "test")
    log4jPath = PropertiesUtil.get("meteor.log4jPath", "/data/apps/spark/conf/log4j.properties")
    appName = PropertiesUtil.get("meteor.appName", "MeteorServer")
    patchSecond = Integer.parseInt(PropertiesUtil.get("meteor.patchSecond", "60"))
    execCronTaskOnStartup = PropertiesUtil.get("meteor.execCronTaskOnStartup", "false")
    isKafkaAuth = PropertiesUtil.get("meteor.isKafkaAuth", "false");

    kafkaClusterHostPorts = PropertiesUtil.get("meteor.kafkaClusterHostPorts", "kafka0:9092,kafka1:9092,kafka2:9092")
    redisMultiHostPorts = PropertiesUtil.get("meteor.redisMultiHostPorts", "multi0=redis0multi0:6379,redis1multi0:6379,redis2multi0:6379")
    redisMaxTotals = Integer.parseInt(PropertiesUtil.get("meteor.redisMaxTotals", "600"))
    redisMaxIdle = Integer.parseInt(PropertiesUtil.get("meteor.redisMaxIdle", "600"))
    zkConnection = PropertiesUtil.get("meteor.zookeeper")

    jdbcDriver = PropertiesUtil.get("meteor.jdbc.driver")
    jdbcUrl = PropertiesUtil.get("meteor.jdbc.url")
    jdbcUsername = PropertiesUtil.get("meteor.jdbc.username")
    jdbcPassword = PropertiesUtil.get("meteor.jdbc.password")

    sshExecCronTaskMachines = StringUtils.split(PropertiesUtil.get("meteor.sshExecCronTaskMachines"), ",")
    cronTaskExecJar = PropertiesUtil.get("meteor.cronTaskExecJar")
    cronTaskLogPath = PropertiesUtil.get("meteor.cronTaskLogPath")

    val excludeTaskIdsStr = PropertiesUtil.get("meteor.excludeTaskIds")
    if (StringUtils.isNotBlank(excludeTaskIdsStr)) {
      excludeTaskIds = StringUtils.split(excludeTaskIdsStr, ",").map { x => Integer.parseInt(StringUtils.trim(x)) }.toList.to[ListBuffer]
    } else {
      excludeTaskIds = ListBuffer[Int]()
    }

    val execSourceTaskIdsStr = PropertiesUtil.get("meteor.execSourceTaskIds")
    if (StringUtils.isNotBlank(execSourceTaskIdsStr)) {
      execSourceTaskIds = StringUtils.split(execSourceTaskIdsStr, ",").map { x => Integer.parseInt(StringUtils.trim(x)) }.toList
    } else {
      execSourceTaskIds = List[Int]()
    }
  }
}