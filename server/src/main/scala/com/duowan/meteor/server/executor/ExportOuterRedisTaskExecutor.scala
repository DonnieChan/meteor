package com.duowan.meteor.server.executor

import java.io.StringReader
import java.util.HashMap

import scala.collection.JavaConversions.mutableMapAsJavaMap
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONObject

import org.apache.commons.lang3.StringUtils
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.sql.DataFrame
import org.mvel2.MVEL

import com.duowan.meteor.model.view.export.ExportOuterRedisTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.CustomSQLUtil
import com.duowan.meteor.server.util.DropTableUtil
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.PerformanceUtil
import com.duowan.meteor.server.util.RedisSingleUtil

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SubSelect

class ExportOuterRedisTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ExportOuterRedisTask]
    val redisScript = task.getRedisScript
    val host = task.getHost
    val port = task.getPort
    val password = task.getPassword
    val fileId = task.getFileId
    val instanceFlowId = instanceTaskExecutor.instanceTask.getInstanceFlowId
    var fetchSql = task.getFetchSql

    if (StringUtils.startsWith(fetchSql, "csql_group_by_1:")) {
      fetchSql = StringUtils.substring(fetchSql, 16)
      execForeachPartition(fetchSql, fileId, host, port, password, redisScript)
    } else {
      var df: DataFrame = null
      var targetTable = ""
      if (StringUtils.startsWith(fetchSql, "csql_group_by_n:")) {
        fetchSql = StringUtils.substring(fetchSql, 16)
        val sqlArr = StringUtils.split(fetchSql, ";")
        val tablePreStr = s"${fileId}_${instanceFlowId}"
        targetTable = CustomSQLUtil.execSql(tablePreStr, sqlArr(0))
        if (StringUtils.isBlank(targetTable)) {
          return
        }
        if (sqlArr.length > 1 && StringUtils.isNotBlank(sqlArr(1))) {
          val finalSql = StringUtils.replace(sqlArr(1), "$targetTable", targetTable)
          df = ExecutorContext.spark.sql(finalSql)
        } else {
          df = ExecutorContext.spark.table(targetTable)
        }
      } else {
        df = ExecutorContext.spark.sql(fetchSql)
      }

      sendRedis(df, fileId, host, port, password, redisScript)
      DropTableUtil.dropTable(targetTable)
    }
  }

  def sendRedis(df: DataFrame, fileId: Integer, host: String, port: Int, password: String, redisScript: String): Unit = {
    df.toJSON.foreachPartition(p => {
      val datas = new java.util.ArrayList[java.util.Map[String, _]]()
      var lastRow: Map[String, _] = Map()
      for (r <- p) {
        val jsonToMap = JSON.parseFull(r).get.asInstanceOf[Map[String, _]]
        lastRow = jsonToMap
        datas.add(jsonToMap.asJava)
      }

      val newExpr = String.format("foreach(row : datas) { var redis = redis; %s}", redisScript)
      val expr = MVEL.compileExpression(newExpr)
      val jedisPool = RedisSingleUtil.getJedisPool(host, port, password)
      var jedis = jedisPool.getResource
      val vars = new HashMap[String, Any]()
      vars.put("datas", datas)
      vars.put("redis", jedis)
      try {
        MVEL.executeExpression(expr, vars)
      } finally {
        jedis.close()
      }

      val jsonStr = JSONObject(lastRow.toMap).toString()
      PerformanceUtil.sendData(jsonStr, fileId)
    })
  }

  /**
   *
   */
  def execForeachPartition(sql: String, fileId: Integer, host: String, port: Int, password: String, redisScript: String): Unit = {
    val stmt = CCJSqlParserUtil.parse(new StringReader(sql))
    val selectBody = stmt.asInstanceOf[Select].getSelectBody().asInstanceOf[PlainSelect]
    val fromTable = selectBody.getFromItem()

    var dataFrame: DataFrame = null
    if (fromTable.isInstanceOf[SubSelect]) {
      dataFrame = ExecutorContext.spark.sql(fromTable.asInstanceOf[SubSelect].getSelectBody.toString())
    } else {
      dataFrame = ExecutorContext.spark.table(fromTable.toString())
    }

    dataFrame.foreachPartition { p =>
      {
        val aggResultMapList = CustomSQLUtil.exec(sql, p)

        var lastRow: scala.collection.mutable.Map[String, Any] = scala.collection.mutable.Map()
        var datas = new java.util.ArrayList[java.util.Map[String, _]]()
        for (aggResultMap <- aggResultMapList) {
          for (aggRowMap <- aggResultMap) {
            datas.add(aggRowMap)
            lastRow = aggRowMap
          }
        }

        val newExpr = String.format("foreach(row : datas) { var redis = redis; %s}", redisScript)
        val expr = MVEL.compileExpression(newExpr)
        val jedisPool = RedisSingleUtil.getJedisPool(host, port, password)
        var jedis = jedisPool.getResource
        val vars = new HashMap[String, Any]()
        vars.put("datas", datas)
        vars.put("redis", jedis)
        try {
          MVEL.executeExpression(expr, vars)
        } finally {
          jedis.close()
        }

        val jsonStr = JSONObject(lastRow.toMap).toString()
        PerformanceUtil.sendData(jsonStr, fileId)
      }
    }
  }

}