package com.duowan.meteor.datasync.executor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.datasync.plugin.ExportMysqlPlugin;
import com.duowan.meteor.datasync.plugin.ImportRedisPlugin;
import com.duowan.meteor.model.view.importredis.ImportMysqlToRedisTask;

public class Mysql2RedisExecutor {

	protected static Logger logger = LoggerFactory.getLogger(Cassandra2HiveExecutor.class);

	public static void exec(ImportMysqlToRedisTask task, Map<String, Object> params) throws Exception {
		ExportMysqlPlugin.exec(task.getFetchSql(), task.getColumns(), task.getMysqlUrl(), task.getMysqlUser(), task.getMysqlPassword(), params);
		ImportRedisPlugin.exec(task, params);
	}
}
