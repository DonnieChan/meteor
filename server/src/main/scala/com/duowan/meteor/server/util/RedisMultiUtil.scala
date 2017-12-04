package com.duowan.meteor.server.util

import scala.collection.mutable.ListBuffer

import org.apache.commons.lang.StringUtils
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

import com.duowan.meteor.server.context.ExecutorContext

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.Protocol

/**
 * Created by chenwu on 2015/5/12 0012.
 */
object RedisMultiUtil extends Logging {

  val jedisPoolListMap = scala.collection.mutable.Map[String, List[JedisPool]]()
  val redisMultiHostPortsArr = StringUtils.split(ExecutorContext.redisMultiHostPorts, "&")

  val config = new GenericObjectPoolConfig()
  config.setMaxTotal(ExecutorContext.redisMaxTotals)
  config.setMaxIdle(ExecutorContext.redisMaxIdle)

  val uvScript = "local result = {} " +
    "local expireTime = ARGV[1] " +
    "for i,pfKey in ipairs(KEYS) do " +
    "  local t = i + 1 " +
    "  local pfVal = ARGV[t] " +
    "  redis.call('pfadd', pfKey, pfVal) " +
    "  local expireV = redis.call('ttl', pfKey) " +
    "  if expireV == -1 then " +
    "    redis.call('expire', pfKey, expireTime) " +
    "  end " +
    "  result[pfKey] = redis.call('pfcount', pfKey) " +
    "end " +
    "local bulkResult = {} " +
    "for k,v in pairs(result) do " +
    "  table.insert(bulkResult, k) " +
    "  table.insert(bulkResult, v) " +
    "end " +
    "return bulkResult "

  val uv2Script = "local result = {} " +
    "local isAccurate = ARGV[1] " +
    "local expireTime = ARGV[2] " +
    "local pos = 2 " +
    "for i,keyItem in ipairs(KEYS) do " +
    "  pos = pos + 1 " +
    "  local len = ARGV[pos] " +
    "  local valItemList = {} " +
    "  for j=1,len do " +
    "    pos = pos + 1 " +
    "    valItemList[j] = ARGV[pos] " +
    "  end " +
    "  result[#result + 1] = keyItem " +
    "  if isAccurate == '1' then " +
    "    redis.call('sadd', keyItem, unpack(valItemList)) " +
    "    result[#result + 1] = redis.call('scard', keyItem) " +
    "  else " +
    "    redis.call('pfadd', keyItem, unpack(valItemList)) " +
    "    result[#result + 1] = redis.call('pfcount', keyItem) " +
    "  end " +
    "  local expireV = redis.call('ttl', keyItem) " +
    "  if expireV == -1 then " +
    "    redis.call('expire', keyItem, expireTime) " +
    "  end " +
    "end " +
    "return result "

  val sumScript = "local result = {} " +
    "local redisKey = ARGV[1] " +
    "local expireTime = ARGV[2] " +
    "for i,keyItem in ipairs(KEYS) do " +
    "  local t = i + 2 " +
    "  local valItem = ARGV[t] " +
    "  result[#result + 1] = keyItem" +
    "  result[#result + 1] = redis.call('hincrby', redisKey, keyItem, valItem) " +
    "end " +
    "local expireVal = redis.call('ttl', redisKey) " +
    "if expireVal == -1 then " +
    "  redis.call('expire', redisKey, expireTime) " +
    "end " +
    "return result "

  val maxScript = "local result = {} " +
    "local redisKey = ARGV[1] " +
    "local expireTime = ARGV[2] " +
    "for i,keyItem in ipairs(KEYS) do " +
    "  local t = i + 2 " +
    "  local valItem = ARGV[t] " +
    "  local sValItem = redis.call('hget', redisKey, keyItem) " +
    "  if type(sValItem) == 'boolean' or tonumber(sValItem) < tonumber(valItem) then " +
    "    redis.call('hset', redisKey, keyItem, valItem) " +
    "    sValItem = valItem " +
    "  end " +
    "  result[#result + 1] = keyItem " +
    "  result[#result + 1] = sValItem " +
    "end " +
    "local expireVal = redis.call('ttl', redisKey) " +
    "if expireVal == -1 then " +
    "  redis.call('expire', redisKey, expireTime) " +
    "end " +
    "return result "

  val minScript = "local result = {} " +
    "local redisKey = ARGV[1] " +
    "local expireTime = ARGV[2] " +
    "for i,keyItem in ipairs(KEYS) do " +
    "  local t = i + 2 " +
    "  local valItem = ARGV[t] " +
    "  local sValItem = redis.call('hget', redisKey, keyItem) " +
    "  if type(sValItem) == 'boolean' or tonumber(sValItem) > tonumber(valItem) then " +
    "    redis.call('hset', redisKey, keyItem, valItem) " +
    "    sValItem = valItem " +
    "  end " +
    "  result[#result + 1] = keyItem " +
    "  result[#result + 1] = sValItem " +
    "end " +
    "local expireVal = redis.call('ttl', redisKey) " +
    "if expireVal == -1 then " +
    "  redis.call('expire', redisKey, expireTime) " +
    "end " +
    "return result "

  var uvScriptSha1: String = null
  var uv2ScriptSha1: String = null
  var sumScriptSha1: String = null
  var maxScriptSha1: String = null
  var minScriptSha1: String = null

  for (nameHostPorts: String <- redisMultiHostPortsArr) {
    logInfo(s"new redis multi pool: $nameHostPorts")
    val nameHostPortsSplit = StringUtils.split(nameHostPorts, "=")
    val redisMultiName = nameHostPortsSplit(0)
    val jedisPoolList = ListBuffer[JedisPool]()
    val hostPostsSplit = StringUtils.split(nameHostPortsSplit(1), ",")
    var jedisPool: JedisPool = null
    for (hostPort: String <- hostPostsSplit) {
      val hostPostArr = StringUtils.split(hostPort, ":")
      jedisPool = new JedisPool(config, hostPostArr(0), hostPostArr(1).toInt, ExecutorContext.redisTimeout, null, Protocol.DEFAULT_DATABASE)
      jedisPoolList += jedisPool
      var jedis: Jedis = null
      try {
        jedis = jedisPool.getResource
        uvScriptSha1 = jedis.scriptLoad(uvScript)
        uv2ScriptSha1 = jedis.scriptLoad(uv2Script)
        sumScriptSha1 = jedis.scriptLoad(sumScript)
        maxScriptSha1 = jedis.scriptLoad(maxScript)
        minScriptSha1 = jedis.scriptLoad(minScript)
        logInfo(s"uvScriptSha1=$uvScriptSha1, uv2ScriptSha1=$uv2ScriptSha1, sumScriptSha1=$sumScriptSha1, maxScriptSha1=$maxScriptSha1, minScriptSha1=$minScriptSha1")
      } finally {
        jedis.close
      }
    }
    jedisPoolListMap.put(redisMultiName, jedisPoolList.toList)
  }

  def getResourceList(redisMultiName: String): List[Jedis] = {
    val jedisPoolList = jedisPoolListMap.getOrElse(redisMultiName, null)
    val jedisList = ListBuffer[Jedis]()
    for (jedisPool: JedisPool <- jedisPoolList) {
      jedisList += jedisPool.getResource
    }
    jedisList.toList
  }

}
