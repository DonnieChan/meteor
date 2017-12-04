package com.duowan.meteor.datasync.executor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.datasync.plugin.ExportHivePlugin;
import com.duowan.meteor.datasync.plugin.ImportCassandraPlugin;
import com.duowan.meteor.model.view.importcassandra.ImportHiveToCassandraTask;

public class Hive2CassandraExecutor {

	protected static Logger logger = LoggerFactory.getLogger(Hive2CassandraExecutor.class);

	public static void exec(ImportHiveToCassandraTask task, Map<String, Object> params) throws Exception {
		ExportHivePlugin.exec(task, params);
		ImportCassandraPlugin.exec(task, params);
	}
}
