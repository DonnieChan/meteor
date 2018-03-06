package com.duowan.meteor.server.executor

import java.io.StringReader

import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.JavaConversions.mutableMapAsJavaMap
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.util.parsing.json.JSON
import scala.util.parsing.json.JSONObject

import org.apache.commons.lang3.StringUtils
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.sql.DataFrame

import com.duowan.meteor.model.view.export.ExportJDBCTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.CustomSQLUtil
import com.duowan.meteor.server.util.DropTableUtil
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.PerformanceUtil
import com.duowan.meteor.util.JdbcTemplateProvider

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SubSelect

class ExportJDBCTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ExportJDBCTask]
    val jdbcDriver = task.getJdbcDriver
    val jdbcUrl = task.getJdbcUrl
    val jdbcUsername = task.getJdbcUsername
    val jdbcPassword = task.getJdbcPassword
    val fileId = task.getFileId
    val sourceTaskId = instanceTaskExecutor.instanceFlowExecutor.instanceFlow.getSourceTaskId
    val instanceFlowId = instanceTaskExecutor.instanceTask.getInstanceFlowId
    var fetchSql = task.getFetchSql

    if (StringUtils.startsWith(fetchSql, "csql_group_by_1:")) {
      fetchSql = StringUtils.substring(fetchSql, 16)
      execForeachPartition(fetchSql, fileId, jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword, task.getInsertSql, sourceTaskId)
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

      sendJDBC(df, fileId, jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword, task.getInsertSql, sourceTaskId)
      DropTableUtil.dropTable(targetTable)
    }
  }

  def sendJDBC(df: DataFrame, fileId: Integer, jdbcDriver: String, jdbcUrl: String, jdbcUsername: String, jdbcPassword: String, insertSql: String, sourceTaskId: Integer): Unit = {
    df.toJSON.foreachPartition(p => {
      val jdbcTemplate = JdbcTemplateProvider.getJdbcTemplate(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword)
      var lastRow: Map[String, _] = Map()
      var i = 0
      var result = scala.collection.mutable.ListBuffer[java.util.Map[String, _]]()

      for (r <- p) {
        val jsonToMap = JSON.parseFull(r).get.asInstanceOf[Map[String, _]]
        i += 1
        result += jsonToMap.asJava
        lastRow = jsonToMap
        if (i == 5000) {
          jdbcTemplate.batchUpdate(insertSql, result.toArray)
          i = 0
          result = scala.collection.mutable.ListBuffer[java.util.Map[String, _]]()
        }
      }

      if (i > 0) {
        jdbcTemplate.batchUpdate(insertSql, result.toArray)
      }

      val jsonStr = JSONObject(lastRow).toString()
      PerformanceUtil.sendData(jsonStr, fileId, sourceTaskId)
    })
  }

  /**
   *
   */
  def execForeachPartition(sql: String, fileId: Integer, jdbcDriver: String, jdbcUrl: String, jdbcUsername: String, jdbcPassword: String, insertSql: String, sourceTaskId: Integer): Unit = {
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
        var i = 0
        var result = scala.collection.mutable.ListBuffer[java.util.Map[String, _]]()
        val jdbcTemplate = JdbcTemplateProvider.getJdbcTemplate(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword)

        for (aggResultMap <- aggResultMapList) {
          for (aggRowMap <- aggResultMap) {
            i += 1
            result += aggRowMap
            lastRow = aggRowMap
            if (i == 5000) {
              jdbcTemplate.batchUpdate(insertSql, result.toArray)
              i = 0
              result = scala.collection.mutable.ListBuffer[java.util.Map[String, _]]()
            }
          }
        }

        if (result.size() > 0) {
          jdbcTemplate.batchUpdate(insertSql, result.toArray)
        }

        val jsonStr = JSONObject(lastRow.toMap).toString()
        PerformanceUtil.sendData(jsonStr, fileId, sourceTaskId)
      }
    }
  }
}