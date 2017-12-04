package com.duowan.meteor.server.executor

import org.apache.commons.lang.StringUtils

import com.duowan.meteor.model.view.buildmodel.SqlTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.CustomSQLUtil
import com.duowan.meteor.server.util.DropTableUtil
import com.duowan.meteor.server.util.Logging

class SqlTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[SqlTask]

    if (StringUtils.startsWith(task.getSql, "csql_group_by_n:")) {
      val sql = StringUtils.substring(task.getSql, 16)
      val sqlArr = StringUtils.split(sql, ";")
      val tablePreStr = s"${task.getFileId}_${instanceTaskExecutor.instanceTask.getInstanceFlowId}"
      val targetTable = CustomSQLUtil.execSql(tablePreStr, sqlArr(0))
      if (sqlArr.length > 1 && StringUtils.isNotBlank(sqlArr(1))) {
        val finalSql = StringUtils.replace(sqlArr(1), "$targetTable", targetTable)
        ExecutorContext.spark.sql(finalSql)
      }
      DropTableUtil.dropTable(targetTable)
    } else {
      ExecutorContext.spark.sql(task.getSql)
    }

    if (task.getRepartition != null && task.getRepartition > 0) {
      val tmpTable = s"${instanceTaskExecutor.table}_tmp"
      ExecutorContext.spark.sql(s"CACHE TABLE ${tmpTable} AS SELECT * FROM ${instanceTaskExecutor.table}")
      ExecutorContext.spark.catalog.dropTempView(instanceTaskExecutor.table)
      ExecutorContext.spark.table(tmpTable).repartition(task.getRepartition).createOrReplaceTempView(instanceTaskExecutor.table)
      ExecutorContext.spark.sql(s"CACHE TABLE ${instanceTaskExecutor.table}")
      DropTableUtil.dropTable(tmpTable)
    }
  }

}