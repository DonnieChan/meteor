package com.duowan.meteor.server.executor

import scala.collection.mutable.ListBuffer
import scala.util.parsing.json.JSON

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang3.StringUtils

import com.duowan.meteor.model.view.export.ExportRedisTask
import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.executor.instance.InstanceTaskExecutor
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.RedisMultiUtil

class ExportRedisTaskExecutor extends AbstractTaskExecutor with Logging {

  override def exec(instanceTaskExecutor: InstanceTaskExecutor, paramMap: Map[String, Any]): Unit = {
    val task = instanceTaskExecutor.instanceTask.getTask.asInstanceOf[ExportRedisTask]
    ExecutorContext.spark.sql(task.getFetchSql).toJSON.foreachPartition(p => {
      val jedisList = RedisMultiUtil.getResourceList(task.getRedisMultiName)
      val jedisListSzie = jedisList.size
      try {
        for (r <- p) {
          val jsonToMap = JSON.parseFull(r).get.asInstanceOf[Map[String, String]]

          var tableKeyData = new ListBuffer[String]()
          val tableKeys = StringUtils.split(task.getTableKeys, ",")
          for (tableKey <- tableKeys) {
            tableKeyData += jsonToMap.getOrElse(StringUtils.trim(tableKey), null)
          }

          val dataKey = task.getToTable + "|" + tableKeyData.mkString("|")
          val dataKeyMD5 = DigestUtils.md5Hex(dataKey)
          val index = Math.abs(dataKeyMD5.hashCode() % jedisListSzie)
          val jedis = jedisList(index)

          if (task.getIsOverride == 0) {
            val setResult = jedis.setnx(dataKeyMD5, r)
            if (setResult == 1) {
              jedis.expire(dataKeyMD5, task.getExpireSeconds)
            }
          } else {
            jedis.setex(dataKeyMD5, task.getExpireSeconds, r)
          }
        }
      } finally {
        jedisList.map(jedis => jedis.close)
      }
    })
  }

}