package com.duowan.meteor.server.executor

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON

import org.apache.commons.lang3.StringUtils

import com.duowan.meteor.model.view.export.ExportRedisTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.RedisClusterUtil

class ExportRedisTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ExportRedisTask]
    ExecutorContext.spark.sql(task.getFetchSql).toJSON.foreachPartition(p => {
      for (r <- p) {
        val jsonToMap = JSON.parseFull(r).get.asInstanceOf[Map[String, String]]
        var tableKeyData = new ListBuffer[String]()

        if (StringUtils.isNotBlank(task.getPartitionKey)) {
          tableKeyData += jsonToMap.getOrElse(StringUtils.trim(task.getPartitionKey), null)
        }

        val tableKeys = StringUtils.split(task.getTableKeys, ",")
        for (tableKey <- tableKeys) {
          tableKeyData += jsonToMap.getOrElse(StringUtils.trim(tableKey), null)
        }

        val dataId = tableKeyData.mkString("|")
        val dataKey = task.getToTable + "|" + dataId

        if (task.getIsOverride == 0) {
          RedisClusterUtil.setneex(dataKey, r, task.getExpireSeconds)
        } else {
          RedisClusterUtil.setex(dataKey, r, task.getExpireSeconds)
        }
      }
    })
  }

}