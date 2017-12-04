package com.duowan.meteor.datasync.plugin;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.model.view.export.ExportCassandraToHiveTask;
import com.duowan.meteor.util.ApacheCommonsExecutor;
import com.duowan.meteor.util.FreemarkerUtil;

/**
 * Created by huangshaoqian on 2015/11/18.
 */
public class ImportHivePlugin {

	private static Logger logger = LoggerFactory.getLogger(ImportHivePlugin.class);

	public static void exec(ExportCassandraToHiveTask task, Map<String, Object> params) throws Exception {
		long st = System.currentTimeMillis();
		logger.info("begin import hive table data");
		boolean result = ApacheCommonsExecutor.exec(buildCmd(task, params));
		logger.info("finish import hive table, duration: " + (System.currentTimeMillis() - st));
		if (!result) {
			throw new RuntimeException("But execute hive sql fail!");
		}
	}

	private static String buildCmd(ExportCassandraToHiveTask task, Map<String, Object> params) throws Exception {
		String sqlFile = params.get("tmpPath") + "/import_" + task.getFileId() + "_" + DateFormatUtils.format((Date) params.get("importStartTime"), "yyyyMMddHHmmss") + "_"
				+ DateFormatUtils.format((Date) params.get("importEndTime"), "yyyyMMddHHmmss") + ".sql";
		String sql = FreemarkerUtil.parse(task.getLoadSql(), params);
		FileUtils.writeStringToFile(new File(sqlFile), sql, "UTF-8");
		String cmd = "hive -f " + sqlFile;
		logger.info("sql :\n" + sql);
		logger.info("excute hive cmd: " + cmd);
		return cmd;
	}
}
