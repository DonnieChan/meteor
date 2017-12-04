package com.duowan.meteor.server.util;

import redis.clients.jedis.JedisCluster;

public class RedisClusterJavaUtil {

	public static Long sadd(JedisCluster jc, String key, String[] valArr) {
		return jc.sadd(key, valArr);
	}
	
	public static Long pfadd(JedisCluster jc, String key, String[] valArr) {
		return jc.pfadd(key, valArr);
	}
}
