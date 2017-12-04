package com.duowan.meteor.server.util;

import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public class RedisSingleJavaUtil {

	public static void saddPipeline(Pipeline pipeline, String key, String[] valArr) {
		pipeline.sadd(key, valArr);
	}
	
	public static void pfaddPipeline(Pipeline pipeline, String key, String[] valArr) {
		pipeline.pfadd(key, valArr);
	}
	
	public static Response<Long> pfcountPipeline(Pipeline pipeline, String key) {
		return pipeline.pfcount(key);
	}
}
