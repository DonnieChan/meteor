package com.duowan.meteor.server.factory

import java.util.Date
import java.util.UUID
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import scala.collection.JavaConversions.asScalaSet
import org.apache.commons.lang3.StringUtils
import com.duowan.meteor.model.enumtype.ExecStatus
import com.duowan.meteor.model.view.AbstractTaskDepend
import com.duowan.meteor.model.view.buildmodel.SqlTask
import com.duowan.meteor.model.view.export.ExportCassandraTask
import com.duowan.meteor.model.view.export.ExportKafkaTask
import com.duowan.meteor.model.view.export.ExportRedisTask
import com.duowan.meteor.model.view.importqueue.ImportKafkaTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceFlowExecutor
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.model.view.export.ExportJDBCTask
import com.duowan.meteor.model.view.export.ExportOuterRedisTask
import com.duowan.meteor.util.FreemarkerUtil
import com.duowan.meteor.server.util.XThreadPoolExecutor
import java.util.concurrent.PriorityBlockingQueue

object InstanceFlowExecutorFactory extends Logging {

  def getInstanceFlowExecutor(sourceTaskId: Integer): InstanceFlowExecutor = {
    val flowExecutor = initInstanceFlowExecutor(sourceTaskId, UUID.randomUUID().toString().replace("-", ""))
    flowExecutor
  }

  def initInstanceFlowExecutor(sourceTaskId: Integer, instanceFlowId: String): InstanceFlowExecutor = {
    val flowExecutor = new InstanceFlowExecutor()
    flowExecutor.instanceFlow.setInstanceFlowId(instanceFlowId)
    flowExecutor.instanceFlow.setInitTime(new Date())
    flowExecutor.instanceFlow.setSourceTaskId(sourceTaskId)
    flowExecutor.instanceFlow.setStatus(ExecStatus.Init.name())
    initInstanceTaskExecutor(flowExecutor, sourceTaskId)
    flowExecutor
  }

  def initInstanceTaskExecutor(flowExecutor: InstanceFlowExecutor, taskId: Integer): Unit = {
    if (flowExecutor.taskExecutorMap.contains(taskId)) {
      return
    }
    val taskExecutor = new InstanceTaskExecutor()
    flowExecutor.taskExecutorMap += taskId -> taskExecutor
    taskExecutor.instanceFlowExecutor = flowExecutor

    val task = DefTaskFactory.getCloneById(taskId).asInstanceOf[AbstractTaskDepend]
    val instanceFlowId = flowExecutor.instanceFlow.getInstanceFlowId
    taskExecutor.instanceTask.setInstanceFlowId(instanceFlowId)
    taskExecutor.instanceTask.setStatus(ExecStatus.Init.name())
    taskExecutor.instanceTask.setTask(task)

    for (e <- task.getPreDependSet) {
      taskExecutor.unFinishedPreSet += e
    }
    for (e <- task.getPostDependSet) {
      taskExecutor.unFinishedPostSet += e
    }

    task match {
      case t: ImportKafkaTask => {
        t.setRegTable(s"${t.getRegTable}_$instanceFlowId")
        taskExecutor.table = t.getRegTable
      }

      case t: SqlTask => {
        val cachePattern = "(?i)cache\\s+table\\s+([\\w\\d]+)\\s+as";
        val cacheTable = getByPattern(cachePattern, t.getSql)
        val targetCacheTable = s"${cacheTable}_$instanceFlowId"
        val targetCacheSql = StringUtils.replacePattern(t.getSql, cachePattern, s"cache table $targetCacheTable as")
        if (StringUtils.isNotBlank(cacheTable)) {
          taskExecutor.table = targetCacheTable
        }

        var targetFromSql = replaceFrom(targetCacheSql, instanceFlowId)
        targetFromSql = FreemarkerUtil.parse(targetFromSql)
        t.setSql(targetFromSql)
      }

      case t: ExportKafkaTask => {
        if (!StringUtils.equals(System.getenv("DWENV"), "prod")) {
          t.setToBrokers(ExecutorContext.kafkaClusterHostPorts)
        }

        var targetFromSql = replaceFrom(t.getFetchSql, instanceFlowId)
        targetFromSql = FreemarkerUtil.parse(targetFromSql)
        t.setFetchSql(targetFromSql)
      }
      
      case t: ExportJDBCTask => {
        var targetFromSql = replaceFrom(t.getFetchSql, instanceFlowId)
        targetFromSql = FreemarkerUtil.parse(targetFromSql)
        t.setFetchSql(targetFromSql)
      }

      case t: ExportCassandraTask => {
        var targetFromSql = replaceFrom(t.getFetchSql, instanceFlowId)
        targetFromSql = FreemarkerUtil.parse(targetFromSql)
        t.setFetchSql(targetFromSql)
      }

      case t: ExportRedisTask => {
        var targetFromSql = replaceFrom(t.getFetchSql, instanceFlowId)
        targetFromSql = FreemarkerUtil.parse(targetFromSql)
        t.setFetchSql(targetFromSql)
      }
      
      case t: ExportOuterRedisTask => {
        var targetFromSql = replaceFrom(t.getFetchSql, instanceFlowId)
        targetFromSql = FreemarkerUtil.parse(targetFromSql)
        t.setFetchSql(targetFromSql)
      }

      case _ =>
    }

    if (!TaskThreadPoolFactory.threadPoolMap.contains(taskId)) {
      val threadPool = new XThreadPoolExecutor(task.getThreadPoolSize, task.getThreadPoolSize, 300L, TimeUnit.SECONDS, new PriorityBlockingQueue[Runnable](), new ThreadPoolExecutor.CallerRunsPolicy())
      TaskThreadPoolFactory.threadPoolMap += taskId -> threadPool
    }

    for (e <- taskExecutor.instanceTask.getTask.getPostDependSet) {
      initInstanceTaskExecutor(flowExecutor, e)
    }
  }

  def getByPattern(pattern: String, s: String): String = {
    val p = Pattern.compile(pattern)
    val m = p.matcher(s)
    var result: String = ""
    if (m.find) {
      result = m.group(1)
    }
    result
  }

  def replaceFrom(sql: String, instanceFlowId: String): String = {
    var targetFromSql = sql
    val regex = "(?i)\\s+from\\s+([\\w]+)"
    val pattern = Pattern.compile(regex)
    val matcher = pattern.matcher(sql)
    val tableNameSet = scala.collection.mutable.Set[String]()
    while (matcher.find()) {
      tableNameSet.add(matcher.group(1))
    }
    for (tableName <- tableNameSet) {
      targetFromSql = StringUtils.replacePattern(targetFromSql, s"(?i)\\s+from\\s+${tableName}(\\s" + "|$)", s" from ${tableName}_${instanceFlowId} ")
      targetFromSql = StringUtils.replacePattern(targetFromSql, s"(?i)\\s+from\\s+${tableName}\\)", s" from ${tableName}_${instanceFlowId})")
    }
    targetFromSql
  }

}