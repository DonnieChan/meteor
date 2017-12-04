package com.duowan.meteor.model.view.export;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/7 0007.
 */
public class ExportJDBCTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 898602025665135111L;
	public final String CLASS = "com.duowan.meteor.model.view.export.ExportJDBCTask";

	private String jdbcDriver;

	private String jdbcUrl;

	private String jdbcUsername;
	
	private String jdbcPassword;
	
	private String fetchSql;
	
	private String insertSql;

    public ExportJDBCTask() {
        this.setFileType(FileType.ExportJDBC.name());
        this.setProgramClass("com.duowan.meteor.server.executor.ExportJDBCTaskExecutor");
    }
    
    @Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.jdbcDriver), "jdbcDriver cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.jdbcUrl), "jdbcUrl cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.jdbcUsername), "jdbcUsername cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.jdbcPassword), "jdbcPassword cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.fetchSql), "fetchSql cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.insertSql), "insertSql cannot be blank!");
	}
    
    @Override
	public void doTrim() {
		super.doTrim();
		jdbcDriver = StringUtils.trim(jdbcDriver);
		jdbcUrl = StringUtils.trim(jdbcUrl);
		jdbcUsername = StringUtils.trim(jdbcUsername);
		jdbcPassword = StringUtils.trim(jdbcPassword);
		fetchSql = StringUtils.trim(fetchSql);
		insertSql = StringUtils.trim(insertSql);
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getJdbcUsername() {
		return jdbcUsername;
	}

	public void setJdbcUsername(String jdbcUsername) {
		this.jdbcUsername = jdbcUsername;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public String getFetchSql() {
		return fetchSql;
	}

	public void setFetchSql(String fetchSql) {
		this.fetchSql = fetchSql;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}
}
