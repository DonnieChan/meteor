package com.duowan.meteor.model.view.export;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/7 0007.
 */
public class ExportOuterRedisTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 8986020257751354030L;
	public final String CLASS = "com.duowan.meteor.model.view.export.ExportOuterRedisTask";

    private String host;
    private Integer port = 6379;
    private String password = null; 
    private String fetchSql;
    private String redisScript;

    public ExportOuterRedisTask() {
        this.setFileType(FileType.ExportOuterRedis.name());
        this.setProgramClass("com.duowan.meteor.server.executor.ExportOuterRedisTaskExecutor");
    }
    
    @Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.host), "host cannot be blank!");
		assertTrue(this.port != null, "port cannot be null!");
		assertTrue(StringUtils.isNotBlank(this.fetchSql), "fetchSql cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.redisScript), "redisScript cannot be blank!");
	}
    
    @Override
	public void doTrim() {
		super.doTrim();
		host = StringUtils.trim(host);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFetchSql() {
		return fetchSql;
	}

	public void setFetchSql(String fetchSql) {
		this.fetchSql = fetchSql;
	}

	public String getRedisScript() {
		return redisScript;
	}

	public void setRedisScript(String redisScript) {
		this.redisScript = redisScript;
	}

}
