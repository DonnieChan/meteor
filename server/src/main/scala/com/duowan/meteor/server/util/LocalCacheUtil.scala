package com.duowan.meteor.server.util

import java.util.concurrent.TimeUnit

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder

/**
 * Created by chenwu on 2015/8/19 0019.
 */
object LocalCacheUtil extends Logging {

  val cache: Cache[String, String] = CacheBuilder.newBuilder().maximumSize(10000000).expireAfterWrite(30, TimeUnit.MINUTES).expireAfterAccess(10, TimeUnit.MINUTES).build()

  def get(key: String): String = {
    cache.getIfPresent(key)
  }

  def put(key: String, value: String): Unit = {
    cache.put(key, value)
  }

}
