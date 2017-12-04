package com.duowan.meteor.model.view.importcassandra;

import com.duowan.meteor.model.enumtype.FileType;

/**
 * Created by Administrator on 2015/10/16.
 */
public class ImportMysqlToCassandraTask extends ImportCassandraTask {

    private static final long serialVersionUID = 7086216515404713371L;
    public final String CLASS = "com.duowan.meteor.model.view.importcassandra.ImportMysqlToCassandraTask";
    
    private String mysqlUser;
    private String mysqlPassword;
    private String mysqlUrl;
    private String fetchSql;

    public ImportMysqlToCassandraTask() {
        this.setFileType(FileType.Mysql2Cassandra.name());
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

}
