package com.duowan.meteor.datasync.executor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.datasync.plugin.ExportCassandraPlugin;
import com.duowan.meteor.datasync.plugin.ImportHivePlugin;
import com.duowan.meteor.model.view.export.ExportCassandraToHiveTask;

public class Cassandra2HiveExecutor {

	protected static Logger logger = LoggerFactory.getLogger(Cassandra2HiveExecutor.class);

	public static void exec(ExportCassandraToHiveTask task, Map<String, Object> params) throws Exception {
		ExportCassandraPlugin.exec(task, params);
		ImportHivePlugin.exec(task, params);
	}
}
