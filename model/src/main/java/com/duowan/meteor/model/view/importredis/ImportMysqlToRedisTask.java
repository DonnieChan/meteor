package com.duowan.meteor.model.view.importredis;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by Administrator on 2015/10/16.
 */
public class ImportMysqlToRedisTask extends AbstractTaskDepend {

    private static final long serialVersionUID = 7086216515404713371L;
    public final String CLASS = "com.duowan.meteor.model.view.importcassandra.ImportMysqlToRedisTask";
    
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlUrl;
    private String fetchSql;
    
    private String table;
    private String tableKeys;
    private String columns;
    private Integer expireSeconds = 60 * 60 * 24;
    private String redisMultiName;

    public ImportMysqlToRedisTask() {
        this.setFileType(FileType.Mysql2Redis.name());
        this.setProgramClass("com.duowan.meteor.server.executor.ShellTaskExecutor");
    }
    
    public String getFetchSql() {
        return fetchSql;
    }

    public void setFetchSql(String fetchSql) {
        this.fetchSql = fetchSql;
    }

    public String getMysqlUser() {
        return mysqlUser;
    }

    public void setMysqlUser(String mysqlUser) {
        this.mysqlUser = mysqlUser;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public void setMysqlPassword(String mysqlPassword) {
        this.mysqlPassword = mysqlPassword;
    }

    public String getMysqlUrl() {
        return mysqlUrl;
    }

    public void setMysqlUrl(String mysqlUrl) {
        this.mysqlUrl = mysqlUrl;
    }

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getTableKeys() {
		return tableKeys;
	}

	public void setTableKeys(String tableKeys) {
		this.tableKeys = tableKeys;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public Integer getExpireSeconds() {
		return expireSeconds;
	}

	public void setExpireSeconds(Integer expireSeconds) {
		this.expireSeconds = expireSeconds;
	}

	public String getRedisMultiName() {
		return redisMultiName;
	}

	public void setRedisMultiName(String redisMultiName) {
		this.redisMultiName = redisMultiName;
	}
}
