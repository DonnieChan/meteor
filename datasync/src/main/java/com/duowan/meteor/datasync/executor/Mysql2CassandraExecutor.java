package com.duowan.meteor.datasync.executor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.datasync.plugin.ExportMysqlPlugin;
import com.duowan.meteor.datasync.plugin.ImportCassandraPlugin;
import com.duowan.meteor.model.view.importcassandra.ImportMysqlToCassandraTask;

public class Mysql2CassandraExecutor {

	protected static Logger logger = LoggerFactory.getLogger(Cassandra2HiveExecutor.class);

	public static void exec(ImportMysqlToCassandraTask task, Map<String, Object> params) throws Exception {
		ExportMysqlPlugin.exec(task.getFetchSql(), task.getColumns(), task.getMysqlUrl(), task.getMysqlUser(), task.getMysqlPassword(), params);
		ImportCassandraPlugin.exec(task, params);
	}
}
