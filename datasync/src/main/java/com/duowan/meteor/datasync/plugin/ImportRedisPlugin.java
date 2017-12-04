package com.duowan.meteor.datasync.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import com.alibaba.fastjson.JSON;
import com.duowan.meteor.datasync.util.FileHelper;
import com.duowan.meteor.model.view.importredis.ImportMysqlToRedisTask;

/**
 * 
 * @author chenwu
 *
 */
public class ImportRedisPlugin {

	private static Logger logger = LoggerFactory.getLogger(ImportRedisPlugin.class);

	public static void exec(ImportMysqlToRedisTask task, Map<String, Object> params) throws Exception {
		long execStartTime = System.currentTimeMillis();
		logger.info("begin ImportRedis");

		String baseDir = (String) params.get("tmpDataPath");
		List<File> fileList = FileHelper.listFiles(baseDir);
		if (fileList != null && fileList.size() > 0) {
			String redisClusterHostPorts = (String) params.get("redisClusterHostPorts");
			Set<HostAndPort> hostAndPortSet = new HashSet<HostAndPort>();
			String[] redisClusterHostPortArr = StringUtils.split(redisClusterHostPorts, ",");
			for (String hostPort : redisClusterHostPortArr) {
				String[] hostPortSplit = StringUtils.split(hostPort, ":");
				hostAndPortSet.add(new HostAndPort(hostPortSplit[0], Integer.parseInt(hostPortSplit[1])));
			}
			JedisCluster jedisCluster = new JedisCluster(hostAndPortSet);

			int fileNum = fileList.size();
			logger.info("fileNum : " + fileNum);
			for (final File file : fileList) {
				BufferedReader iReader = new BufferedReader(new FileReader(file));
				String line = iReader.readLine();
				String[] columns = task.getColumns().split(",");
				while (line != null) {
					String[] strs = line.split("\u0001");
					Map<String, String> row = new HashMap<String, String>();
					for (int i = 0; i < strs.length; i++) {
						if (StringUtils.isNotBlank(strs[i]) && (!"\\N".equals(strs[i].trim()))) {
							row.put(columns[i].toLowerCase().trim(), strs[i]);
						}
					}
					Object srow = JSON.toJSON(row);
					jedisCluster.setex(task.getTable() + "|" + getTableKey(task, row), task.getExpireSeconds(), srow.toString());
					line = iReader.readLine();
				}
				iReader.close();
			}
			jedisCluster.close();
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
			sb.append(row.get(key)).append("|");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

}