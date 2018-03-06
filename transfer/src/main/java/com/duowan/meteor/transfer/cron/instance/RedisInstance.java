package com.duowan.meteor.transfer.cron.instance;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;


public class RedisInstance {

	private static Logger logger = LoggerFactory.getLogger(AlarmSliceDelayJobInstance.class);
	
	private JedisPool jedisPool;
	private String alarmScriptSha1;
	
	private RedisInstance() {
		String redisHost = System.getProperty("redisHost");
		int redisPort = Integer.parseInt(System.getProperty("redisPort"));
		String redisPassword = System.getProperty("redisPassword");
		logger.info("redisHost={}, redisPort={}, redisPassword={}", new Object[] {redisHost, redisPort, redisPassword});
		jedisPool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort, Protocol.DEFAULT_TIMEOUT, redisPassword, Protocol.DEFAULT_DATABASE);
		Jedis jedis = jedisPool.getResource();
		String script = "local redisKey = KEYS[1] "
				+ "local curTime = ARGV[1] "
				+ "local gap = ARGV[2] "
				+ "local expireTime = ARGV[3] "
				+ "local preTime = redis.call('get', redisKey)"
				+ "if type(preTime) == 'boolean' or tonumber(curTime) - tonumber(preTime) > tonumber(gap) then "
			    + "  redis.call('SETEX', redisKey, expireTime, curTime) "
			    + "  return 1 "
			    + "end "
			    + "return 0 "
				;
		try {
			alarmScriptSha1 = jedis.scriptLoad(script);
		} finally {
			jedis.close();
		}
	}
	
	public static RedisInstance getInstatnce() {
		return RedisInstanceSingletonHolder.instance;
	}

	private static class RedisInstanceSingletonHolder {
		private static RedisInstance instance = new RedisInstance();
	}
	
	public Jedis getJedis() {
		return jedisPool.getResource();
	}
	
	public boolean isAlarm(String alarmKey, int alarmGapMin) {
		Jedis jedis = getJedis();
		Object result = null;
		try {
			result = jedis.evalsha(alarmScriptSha1, Arrays.asList(alarmKey), Arrays.asList(System.currentTimeMillis() + "", alarmGapMin * 60 * 1000l + "", "3600"));
		} finally {
			jedis.close();
		}
		if (result != null && StringUtils.equals(result.toString(), "0")) {
			return false;
		}
		return true;
	}
}
