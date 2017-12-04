package com.duowan.meteor.model.enumtype;

import com.duowan.meteor.model.view.buildmodel.SqlTask;
import com.duowan.meteor.model.view.cron.CronTask;
import com.duowan.meteor.model.view.export.ExportCassandraTask;
import com.duowan.meteor.model.view.export.ExportCassandraToHiveTask;
import com.duowan.meteor.model.view.export.ExportJDBCTask;
import com.duowan.meteor.model.view.export.ExportKafkaTask;
import com.duowan.meteor.model.view.export.ExportOuterRedisTask;
import com.duowan.meteor.model.view.export.ExportRedisTask;
import com.duowan.meteor.model.view.importcassandra.ImportHiveToCassandraTask;
import com.duowan.meteor.model.view.importcassandra.ImportMysqlToCassandraTask;
import com.duowan.meteor.model.view.importqueue.ImportKafkaTask;
import com.duowan.meteor.model.view.importredis.ImportMysqlToRedisTask;
import com.duowan.meteor.model.view.other.Dir;

/**
 * 文件类型
 * @author chenwu
 */
public enum FileType {
	
	ImportKafka(FileTypeCategory.Import, ImportKafkaTask.class),
	
	SqlTask(FileTypeCategory.BuildModel, SqlTask.class),
	
	ExportKafka(FileTypeCategory.Export, ExportKafkaTask.class), ExportCassandra(FileTypeCategory.Export, ExportCassandraTask.class), ExportRedis(FileTypeCategory.Export, ExportRedisTask.class), ExportOuterRedis(FileTypeCategory.Export, ExportOuterRedisTask.class), ExportJDBC(FileTypeCategory.Export, ExportJDBCTask.class),
	
	Cron(FileTypeCategory.Cron, CronTask.class),

	Hive2Cassandra(FileTypeCategory.Import, ImportHiveToCassandraTask.class),

	Mysql2Cassandra(FileTypeCategory.Import, ImportMysqlToCassandraTask.class),
	
	Mysql2Redis(FileTypeCategory.Import, ImportMysqlToRedisTask.class),

	Cassandra2Hive(FileTypeCategory.Export, ExportCassandraToHiveTask.class),
	
	Dir(FileTypeCategory.Other, Dir.class);
	
	
	private FileTypeCategory fileTypeCategory;
	private Class<?> refClass;
	
	private FileType(FileTypeCategory fileTypeCategory, Class<?> refClass) {
		this.fileTypeCategory = fileTypeCategory;
		this.refClass = refClass;
	}
	
	public FileTypeCategory getFileTypeCategory() {
		return this.fileTypeCategory;
	}

	public Class<?> getRefClass() {
		return refClass;
	}

	public static FileType getFileTypeByName(String name) {
		return Enum.valueOf(FileType.class, name);
	}
}
