package com.duowan.meteor.server.udf

import scala.collection.mutable.ArrayBuffer

import org.apache.commons.lang3.StringUtils
import org.apache.spark.annotation.DeveloperApi
import org.apache.spark.annotation.Experimental
import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.MutableAggregationBuffer
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction
import org.apache.spark.sql.types.DataType
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.types.MapType
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.types.StructField
import org.apache.spark.sql.types.StructType

import com.duowan.meteor.server.util.LocalCacheUtil
import com.duowan.meteor.server.util.Logging
import com.duowan.meteor.server.util.RedisClusterUtil

class CountDistinctUDAF extends UserDefinedAggregateFunction with Logging {

  def inputSchema: org.apache.spark.sql.types.StructType =
    StructType(StructField("redisSetKey", StringType) :: StructField("partition", StringType) :: StructField("dtKey", StringType) :: StructField("dtValue", StringType) :: StructField("batchSize", IntegerType) :: StructField("redisExpireSeconds", IntegerType) :: Nil)

  def bufferSchema: StructType = StructType(
    StructField("redisSetKeyBuffer", StringType) :: StructField("dtValueBufferMap", MapType(StringType, StringType)) :: StructField("batchSizeBuffer", IntegerType) :: StructField("redisExpireSecondsBuffer", IntegerType) :: Nil)

  def dataType: DataType = LongType

  def deterministic: Boolean = false

  def initialize(buffer: MutableAggregationBuffer): Unit = {
    buffer(0) = ""
    buffer(1) = Map[String, String]()
    buffer(2) = 100
    buffer(3) = 90000
  }

  def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    val redisSetKey = input.getAs[String](0) + "_" + input.getAs[String](1) + "|" + input.getAs[String](2)
    val dtValue = input.getAs[String](3)
    val batchSize = input.getAs[Integer](4)
    val redisExpireSeconds = input.getAs[Integer](5)
    buffer(0) = redisSetKey
    buffer(2) = batchSize
    buffer(3) = redisExpireSeconds
    if (StringUtils.isNotBlank(dtValue)) {
      val dataKey = s"$redisSetKey|$dtValue"
      val localCacheResult = LocalCacheUtil.get(dataKey)
      if (localCacheResult == null) {
        LocalCacheUtil.put(dataKey, "")
        buffer(1) = buffer.getAs[Map[String, String]](1) + (dtValue -> "")
        mapSubmit(buffer, redisSetKey, batchSize, redisExpireSeconds)
      }
    }
  }

  def mapSubmit(buffer: MutableAggregationBuffer, redisSetKey: String, batchSize: Integer, redisExpireSeconds: Integer): Unit = {
    val dtValueBufferMap = buffer.getAs[Map[String, String]](1)
    var dtValueBufferMapSize = dtValueBufferMap.size
    if (dtValueBufferMapSize >= batchSize) {
      var i = 0
      var valArr = ArrayBuffer[String]()
      var finalResult = scala.collection.mutable.Map[String, String]()
      for (keyTmp: String <- dtValueBufferMap.keys) {
        i += 1
        valArr += keyTmp
        finalResult.put(keyTmp, "")
        if (i == batchSize) {
          RedisClusterUtil.saddMulti(redisSetKey, valArr.toArray, redisExpireSeconds)
          i = 0
          valArr = ArrayBuffer[String]()
          finalResult = scala.collection.mutable.Map[String, String]()
        }
      }
      buffer(1) = finalResult.toMap
    }
  }

  def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
    val redisSetKey = buffer2.getAs[String](0)
    val batchSize = buffer2.getAs[Integer](2)
    val redisExpireSeconds = buffer2.getAs[Integer](3)
    buffer1(0) = redisSetKey
    buffer1(1) = buffer1.getAs[Map[String, String]](1) ++ buffer2.getAs[Map[String, String]](1)
    buffer1(2) = batchSize
    buffer1(3) = redisExpireSeconds
    mergetSubmit(buffer1, redisSetKey, batchSize, redisExpireSeconds)
  }

  def mergetSubmit(buffer: MutableAggregationBuffer, redisSetKey: String, batchSize: Integer, redisExpireSeconds: Integer): Unit = {
    val dtValueBufferMap = buffer.getAs[Map[String, String]](1)
    var valArr = ArrayBuffer[String]()
    var i = 0
    for (dtValue: String <- dtValueBufferMap.keys) {
      i += 1
      valArr += dtValue
      if (i == batchSize) {
        RedisClusterUtil.saddMulti(redisSetKey, valArr.toArray, redisExpireSeconds)
        i = 0
        valArr = ArrayBuffer[String]()
      }
    }
    if (valArr.length > 0) {
      RedisClusterUtil.saddMulti(redisSetKey, valArr.toArray, redisExpireSeconds)
    }
    buffer(1) = Map[String, String]()
  }

  def evaluate(buffer: Row): Long = {
    val redisSetKey = buffer.getAs[String](0)
    RedisClusterUtil.scard(redisSetKey)
  }
}  