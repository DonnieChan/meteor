package com.duowan.meteor.server.util

import java.util.HashSet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import scala.collection.mutable.ListBuffer

import org.apache.commons.lang.StringUtils
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

import com.duowan.meteor.server.context.ExecutorContext
import com.duowan.meteor.server.factory.TaskThreadPoolFactory

import redis.clients.jedis.HostAndPort
import redis.clients.jedis.JedisCluster

/**
 * Created by chenwu on 2015/5/12 0012.
 */
object RedisClusterUtil extends Logging {

  val hostAndPortSet = new HashSet[HostAndPort]()
  val redisClusterHostPortArr = StringUtils.split(ExecutorContext.redisClusterHostPorts, ",")
  for (hostPort: String <- redisClusterHostPortArr) {
    val hostPortSplit = StringUtils.split(hostPort, ":")
    hostAndPortSet.add(new HostAndPort(hostPortSplit(0), Integer.parseInt(hostPortSplit(1))))
  }

  val config = new GenericObjectPoolConfig()
  config.setMaxTotal(ExecutorContext.redisMaxTotals)
  config.setMaxIdle(ExecutorContext.redisMaxIdle)
  val jedisCluster = new JedisCluster(hostAndPortSet, ExecutorContext.redisTimeout, 10, config)

  val expireMap = new java.util.concurrent.ConcurrentHashMap[String, Integer]
  val scheduledExecutor = Executors.newScheduledThreadPool(1)

  def setex(key: String, value: String, expireSeconds: Integer): Unit = {
    jedisCluster.setex(key, expireSeconds, value)
  }

  def setneex(key: String, value: String, expireSeconds: Integer): Long = {
    var result = 0L
    var setResult = jedisCluster.setnx(key, value)
    if (setResult == 1) {
      expireMap.put(key, expireSeconds)
      result = 1L
    }
    result
  }

  def get(key: String): String = {
    jedisCluster.get(key)
  }

  def exists(key: String): Boolean = {
    jedisCluster.exists(key)
  }

  def del(key: String): Long = {
    jedisCluster.del(key)
  }

  def hincrBy(hname: String, hkey: String, hval: Long, expireSeconds: Integer): Long = {
    val result = jedisCluster.hincrBy(hname, hkey, Option(hval).getOrElse(0L))
    expireMap.put(hname, expireSeconds)
    result
  }

  def hincrBy(hname: String, hkey: String, hval: Long): Long = {
    jedisCluster.hincrBy(hname, hkey, Option(hval).getOrElse(0L))
  }

  def hget(hname: String, hkey: String): Long = {
    (Option(jedisCluster.hget(hname, hkey)).getOrElse("0")).toLong
  }

  def max(key: String, value: Long, expireSeconds: Integer): Long = {
    expireMap.put(key, expireSeconds)
    max(key, value)
  }

  def max(key: String, value: Long): Long = {
    var maxVal = value.toString
    jedisCluster.zadd(key, value, value.toString)
    val result = jedisCluster.zrevrange(key, 0, 0).iterator()
    if (result.hasNext) {
      maxVal = result.next()
    }
    maxVal.toLong
  }

  def min(key: String, value: Long, expireSeconds: Integer): Long = {
    expireMap.put(key, expireSeconds)
    min(key, value)
  }

  def min(key: String, value: Long): Long = {
    var minVal = value.toString
    jedisCluster.zadd(key, value, value.toString)
    val result = jedisCluster.zrange(key, 0, 0).iterator()
    if (result.hasNext) {
      minVal = result.next()
    }
    minVal.toLong
  }

  def saddMulti(key: String, valArr: Array[String], expireSeconds: Integer): Long = {
    expireMap.put(key, expireSeconds)
    saddMulti(key, valArr)
  }

  def saddMulti(key: String, valArr: Array[String]): Long = {
    RedisClusterJavaUtil.sadd(jedisCluster, key, valArr)
  }

  def scard(key: String): Long = {
    jedisCluster.scard(key)
  }

  def pfadd(key: String, valArr: Array[String]): Long = {
    RedisClusterJavaUtil.pfadd(jedisCluster, key, valArr)
  }

  def pfcount(key: String): Long = {
    jedisCluster.pfcount(key)
  }

  def expire(key: String, expireSeconds: Integer): Unit = {
    expireMap.put(key, expireSeconds)
  }

  def expireKeysThread(toExpireKeyList: ListBuffer[String], expireSeconds: Int): Unit = {
    TaskThreadPoolFactory.cachedThreadPool.submit(new Runnable() {
      override def run(): Unit = {
        try {
          for (redisKey <- toExpireKeyList) {
            if(jedisCluster.ttl(redisKey) == -1) {
              jedisCluster.expire(redisKey, expireSeconds)
            }
          }
        } catch {
          case e: Exception => logError("设置redis过期时间失败!", e)
        }
      }
    })
  }

  scheduledExecutor.scheduleAtFixedRate(new Runnable {
    override def run(): Unit = {
      try {
        val tmpMap = new java.util.HashMap[String, Integer]
        tmpMap.putAll(expireMap)
        val iter = tmpMap.entrySet().iterator()
        while (iter.hasNext()) {
          val entry = iter.next()
          expireMap.remove(entry.getKey)
          if(jedisCluster.ttl(entry.getKey) == -1) {
            jedisCluster.expire(entry.getKey, entry.getValue)
          }
        }
      } catch {
        case e: Exception => logError("设置redis过期时间失败", e)
      }
    }
  }, 1, 1, TimeUnit.MINUTES)
}
