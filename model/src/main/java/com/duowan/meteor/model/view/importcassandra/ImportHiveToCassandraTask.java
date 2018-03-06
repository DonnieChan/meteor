package com.duowan.meteor.model.view.importcassandra;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;

/**
 * Created by huangshaoqian on 2015/10/16.
 */
public class ImportHiveToCassandraTask extends ImportCassandraTask {
	private static final long serialVersionUID = 4348146630873561810L;
	public final String CLASS = "com.duowan.meteor.model.view.importcassandra.ImportHiveToCassandraTask";

	private String fetchSql;

	public ImportHiveToCassandraTask() {
		this.setFileType(FileType.Hive2Cassandra.name());
		this.setProgramClass("com.duowan.meteor.server.executor.ShellTaskExecutor");
	}

	@Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.fetchSql), "fetchSql cannot be blank!");
	}

	@Override
	public void doTrim() {
		super.doTrim();
		fetchSql = StringUtils.trim(fetchSql);
	}

	public String getFetchSql() {
		return fetchSql;
	}

	public void setFetchSql(String fetchSql) {
		this.fetchSql = fetchSql;
	}
}
