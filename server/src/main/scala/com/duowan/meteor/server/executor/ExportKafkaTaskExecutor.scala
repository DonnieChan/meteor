package com.duowan.meteor.server.executor

import java.io.StringReader
import java.util.UUID

import scala.util.parsing.json.JSONObject

import org.apache.commons.lang3.StringUtils
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.spark.annotation.InterfaceStability
import org.apache.spark.sql.DataFrame

import com.duowan.meteor.model.view.export.ExportKafkaTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.context.KafkaProducerSingleton
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.CustomSQLUtil
import com.duowan.meteor.server.util.DropTableUtil
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.PerformanceUtil

import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.Select
import net.sf.jsqlparser.statement.select.SubSelect

class ExportKafkaTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ExportKafkaTask]
    val toBrokers = task.getToBrokers
    val toTopic = task.getToTopic
    val fileId = task.getFileId
    val sourceTaskId = instanceTaskExecutor.instanceFlowExecutor.instanceFlow.getSourceTaskId
    val instanceFlowId = instanceTaskExecutor.instanceTask.getInstanceFlowId
    var fetchSql = task.getFetchSql

    if (StringUtils.startsWith(fetchSql, "csql_group_by_1:")) {
      fetchSql = StringUtils.substring(fetchSql, 16)
      execForeachPartition(fetchSql, toBrokers, toTopic, fileId, sourceTaskId)
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

      sendKafka(df, toBrokers, toTopic, fileId, sourceTaskId)
      DropTableUtil.dropTable(targetTable)
    }
  }

  /**
   *
   */
  def sendKafka(df: DataFrame, toBrokers: String, toTopic: String, fileId: Integer, sourceTaskId: Integer): Unit = {
    df.toJSON.foreachPartition(p => {
      val producer = KafkaProducerSingleton.getInstance(toBrokers)
      var lastRow = ""
      var i = 0
      var partitionKey = UUID.randomUUID().toString

      for (r <- p) {
        i += 1
        val msg = new ProducerRecord[String, String](toTopic, partitionKey, r);
        producer.send(msg)
        lastRow = r
        if (i == 1000) {
          i = 0
          partitionKey = UUID.randomUUID().toString
        }
      }

      PerformanceUtil.sendData(lastRow, fileId, sourceTaskId)
    })
  }

  /**
   *
   */
  def execForeachPartition(sql: String, toBrokers: String, toTopic: String, fileId: Integer, sourceTaskId: Integer): Unit = {
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

        val producer = KafkaProducerSingleton.getInstance(toBrokers)
        var lastRow = ""
        var i = 0
        var partitionKey = UUID.randomUUID().toString

        for (aggResultMap <- aggResultMapList) {
          for (aggRowMap <- aggResultMap) {
            i += 1
            val jsonStr = JSONObject(aggRowMap.toMap).toString()
            val msg = new ProducerRecord[String, String](toTopic, partitionKey, jsonStr)
            producer.send(msg)
            lastRow = jsonStr
            if (i == 1000) {
              i = 0
              partitionKey = UUID.randomUUID().toString
            }
          }
        }

        PerformanceUtil.sendData(lastRow, fileId, sourceTaskId)
      }
    }
  }
}