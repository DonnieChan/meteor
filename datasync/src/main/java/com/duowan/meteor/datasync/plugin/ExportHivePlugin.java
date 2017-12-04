package com.duowan.meteor.datasync.plugin;

import java.io.File;
import java.util.Date;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.model.view.importcassandra.ImportHiveToCassandraTask;
import com.duowan.meteor.util.ApacheCommonsExecutor;
import com.duowan.meteor.util.FreemarkerUtil;

/**
 * Created by haungshaoqian on 2015/10/9
 */
public class ExportHivePlugin {
	
    private static Logger logger = LoggerFactory.getLogger(ExportHivePlugin.class);
    
    public static void exec(ImportHiveToCassandraTask task,Map<String,Object> params) throws Exception {
    	long execStartTime = System.currentTimeMillis();
    	logger.info("begin export hive table data");
    	boolean result = ApacheCommonsExecutor.exec(buildCmd(task, params));
		logger.info("finish export hive table data, duration: " + (System.currentTimeMillis() - execStartTime));
		if (!result) {
			throw new RuntimeException("But execute hive sql fail!");
		}
    }

    private static String buildCmd(ImportHiveToCassandraTask task, Map<String,Object> params) throws Exception {
    	String sqlFile = params.get("tmpPath") + "/export_" + task.getFileId() + "_" + DateFormatUtils.format((Date) params.get("exportStartTime"), "yyyyMMddHHmmss") + "_"
				+ DateFormatUtils.format((Date) params.get("exportEndTime"), "yyyyMMddHHmmss") + ".sql";
		String sql = FreemarkerUtil.parse(replaceSelect(task.getFetchSql()), params);
		FileUtils.writeStringToFile(new File(sqlFile), sql, "UTF-8");
		String cmd = "hive -f " + sqlFile;
		logger.info("sql :\n" + sql);
		logger.info("excute hive cmd: " + cmd);
		return cmd;
    }
    
    private static String replaceSelect(String sql) {
    	String result = sql;
		int index = StringUtils.indexOfIgnoreCase(sql, "SELECT");
		if(index >= 0) {
			result = StringUtils.substring(sql, 0, index) + "insert overwrite local directory '${tmpDataPath}' \n" + StringUtils.substring(sql, index) + " \n";
		}
    	return result;
    }

}
