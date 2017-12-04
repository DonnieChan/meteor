package com.duowan.meteor.server.context

import java.util.concurrent.ConcurrentHashMap

import scala.collection.mutable.ListBuffer

import org.apache.commons.lang3.StringUtils
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.StreamingContext

import com.duowan.meteor.server.util.PropertiesUtil

object ExecutorContext {

  init()

  var DWENV: String = _
  var appName: String = _
  var patchSecond: Int = _
  var execCronTaskOnStartup: String = _

  var kafkaClusterHostPorts: String = _
  var cassandraClusterHosts: String = _
  var redisClusterHostPorts: String = _
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

  def init(): Unit = {
    PropertiesUtil.load("/data/apps/spark/conf/meteor.properties")

    maxPollRecord = Integer.parseInt(PropertiesUtil.get("max.poll.records", "4000"))
    maxPartitionFetchBytes = Integer.parseInt(PropertiesUtil.get("max.partition.fetch.bytes", "5242880"))
    
    DWENV = PropertiesUtil.get("meteor.DWENV", "test")
    appName = PropertiesUtil.get("meteor.appName", "MeteorServer")
    patchSecond = Integer.parseInt(PropertiesUtil.get("meteor.patchSecond", "60"))
    execCronTaskOnStartup = PropertiesUtil.get("meteor.execCronTaskOnStartup", "false")

    kafkaClusterHostPorts = PropertiesUtil.get("meteor.kafkaClusterHostPorts", "cassandra1:9092,cassandra2:9092,cassandra3:9092")
    cassandraClusterHosts = PropertiesUtil.get("meteor.cassandraClusterHosts", "cassandra1,cassandra2,cassandra3")
    redisClusterHostPorts = PropertiesUtil.get("meteor.redisClusterHostPorts", "redis1.meteor.game.yy.com:6379,redis2.meteor.game.yy.com:6379,redis3.meteor.game.yy.com:6379")
    redisMultiHostPorts = PropertiesUtil.get("meteor.redisMultiHostPorts", "huyajf_redis=redis1.multi.huya.com:6379,redis2.multi.huya.com:6379,redis3.multi.huya.com:6379")
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