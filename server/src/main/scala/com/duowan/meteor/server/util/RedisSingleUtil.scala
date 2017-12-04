package com.duowan.meteor.server.util

import org.apache.commons.lang3.StringUtils
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.Protocol
import com.duowan.meteor.server.context.ExecutorContext

/**
 * Created by chenwu on 2015/5/12 0012.
 */
object RedisSingleUtil extends Logging {

  val jedisPoolMap = scala.collection.mutable.Map[String, JedisPool]()
  val config = new GenericObjectPoolConfig()
  config.setMaxTotal(ExecutorContext.redisMaxTotals)
  config.setMaxIdle(ExecutorContext.redisMaxIdle)

  def getJedisPool(host: String, port: Int, password: String): JedisPool = {
    var result = jedisPoolMap.getOrElse(s"$host|$port", null)
    if (result == null) {
      result = initJedisPool(host, port, password)
    }
    result
  }

  def initJedisPool(host: String, port: Int, password: String): JedisPool = synchronized {
    val key = s"$host|$port"
    var result = jedisPoolMap.getOrElse(key, null)
    if (result == null) {
      logInfo(s"new redis pool: $key")
      var tmpPassword = password
      if (StringUtils.isBlank(password)) {
        tmpPassword = null
      }
      result = new JedisPool(config, host, port, ExecutorContext.redisTimeout, tmpPassword, Protocol.DEFAULT_DATABASE)
      jedisPoolMap.put(key, result)
    }
    result
  }

  def getResource(host: String, port: Int, password: String): Jedis = {
    val jedisPool = getJedisPool(host, port, password)
    jedisPool.getResource
  }
}
