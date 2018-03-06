package com.duowan.meteor.server.util

import java.util.concurrent.TimeUnit

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

/**
 * Created by chenwu on 2015/8/19 0019.
 */
object LocalCacheUtil2 extends Logging {

  val cacheMap = scala.collection.mutable.Map[String, Cache[String, String]]()

  def get(tableName: String, key: String): String = {
    val cache = getCacheInstance(tableName)
    cache.getIfPresent(key)
  }

  def put(tableName: String, key: String, value: String): Unit = {
    val cache = getCacheInstance(tableName)
    cache.put(key, value)
  }
  
  def getCacheInstance(tableName: String): Cache[String, String] = {
    var result = cacheMap.getOrElse(tableName, null)
    if (result == null) result = initCacheInstance(tableName)
    result
  }

  def initCacheInstance(tableName: String): Cache[String, String] = synchronized {
    var result = cacheMap.getOrElse(tableName, null)
    if (result == null) {
      result = CacheBuilder.newBuilder().maximumSize(10000000).expireAfterAccess(10, TimeUnit.MINUTES).build()
      cacheMap.put(tableName, result)
    }
    result
  }

}
