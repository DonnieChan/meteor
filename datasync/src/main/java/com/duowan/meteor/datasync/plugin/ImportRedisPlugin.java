package com.duowan.meteor.datasync.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import com.alibaba.fastjson.JSON;
import com.duowan.meteor.datasync.util.FileHelper;
import com.duowan.meteor.model.view.importredis.ImportMysqlToRedisTask;


public class ImportRedisPlugin {

	private static Logger logger = LoggerFactory.getLogger(ImportRedisPlugin.class);

	public static void exec(ImportMysqlToRedisTask task, Map<String, Object> params) throws Exception {
		long execStartTime = System.currentTimeMillis();
		logger.info("begin ImportRedis");

		String baseDir = (String) params.get("tmpDataPath");
		List<File> fileList = FileHelper.listFiles(baseDir);
		if (fileList != null && fileList.size() > 0) {			
			Properties props = (Properties) params.get("props");
			String redisMultiHostPorts = props.getProperty("meteor.redisMultiHostPorts");
			String[] redisMultiHostPortsArr = StringUtils.split(redisMultiHostPorts, "&");
			List<JedisPool> jedisPoolList = new ArrayList<JedisPool>();
			List<Jedis> jedisList = new ArrayList<Jedis>();
			GenericObjectPoolConfig config = new GenericObjectPoolConfig();
			int redisTimeout = 30000;
			for (String nameHostPorts : redisMultiHostPortsArr) {
				String[] nameHostPortsSplit = StringUtils.split(nameHostPorts, "=");
				if (StringUtils.equals(task.getRedisMultiName(), nameHostPortsSplit[0])) {
					String[] hostPostsSplit = StringUtils.split(nameHostPortsSplit[1], ",");
					for (String hostPort : hostPostsSplit) {
						String[] hostPostArr = StringUtils.split(hostPort, ":");
						JedisPool jedisPool = new JedisPool(config, hostPostArr[0], Integer.parseInt(hostPostArr[1]), redisTimeout, null, Protocol.DEFAULT_DATABASE);
						jedisPoolList.add(jedisPool);
						jedisList.add(jedisPool.getResource());
					}
					break;
				}
			}

			int fileNum = fileList.size();
			logger.info("fileNum : " + fileNum);
			String[] columns = task.getColumns().split(",");
			for (final File file : fileList) {
				BufferedReader iReader = new BufferedReader(new FileReader(file));
				String line = iReader.readLine();
				while (line != null) {
					String[] strs = line.split("\u0001");
					Map<String, String> row = new HashMap<String, String>();
					for (int i = 0; i < strs.length; i++) {
						if (StringUtils.isNotBlank(strs[i]) && (!"\\N".equals(strs[i].trim()))) {
							row.put(columns[i].toLowerCase().trim(), strs[i]);
						}
					}
					Object srow = JSON.toJSON(row);
					String tableKey = getTableKey(task, row);
					String tableKeyMD5 = DigestUtils.md5Hex(tableKey);
					int index = Math.abs(tableKeyMD5.hashCode() % jedisList.size());
					jedisList.get(index).setex(tableKeyMD5, task.getExpireSeconds(), srow.toString());
					line = iReader.readLine();
				}
				iReader.close();
			}
			
			for (int i=0; i<jedisList.size(); i++) {
				jedisList.get(i).close();
				jedisPoolList.get(i).close();
			}
		}
		logger.info("finish import redis, duration: " + (System.currentTimeMillis() - execStartTime));
	}

	/**
	 * get table key
	 * 
	 * @param row
	 * @return
	 */
	private static String getTableKey(ImportMysqlToRedisTask task, Map<String, String> row) {
		StringBuffer sb = new StringBuffer();
		for (String key : task.getTableKeys().split(",")) {
			sb.append(row.get(StringUtils.trim(key))).append("|");
		}
		sb.delete(sb.length() - 1, sb.length());
		return task.getTable() + "|" + sb.toString();
	}

}