package com.duowan.meteor.model.view.export;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/7 0007.
 */
public class ExportRedisTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 8986020256651354030L;
	public final String CLASS = "com.duowan.meteor.model.view.export.ExportRedisTask";

	private String redisMultiName = "";
    private String toTable;
    private String tableKeys;
    private Integer isOverride = 1; 
    private String fetchSql;
    private Integer expireSeconds = 30 * 60;

    public ExportRedisTask() {
        this.setFileType(FileType.ExportRedis.name());
        this.setProgramClass("com.duowan.meteor.server.executor.ExportRedisTaskExecutor");
    }
    
    @Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.toTable), "toTable cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.tableKeys), "tableKeys cannot be blank!");
		assertTrue(isOverride != null, "isOverride cannot be null!");
		assertTrue(StringUtils.isNotBlank(this.fetchSql), "fetchSql cannot be blank!");
	}
    
    @Override
	public void doTrim() {
		super.doTrim();
		toTable = StringUtils.trim(toTable);
		tableKeys = StringUtils.trim(tableKeys);
		fetchSql = StringUtils.trim(fetchSql);
	}

	public String getToTable() {
		return toTable;
	}

	public void setToTable(String toTable) {
		this.toTable = toTable;
	}

	public String getTableKeys() {
		return tableKeys;
	}

	public void setTableKeys(String tableKeys) {
		this.tableKeys = tableKeys;
	}

	public Integer getIsOverride() {
		return isOverride;
	}

	public void setIsOverride(Integer isOverride) {
		this.isOverride = isOverride;
	}

	public String getFetchSql() {
		return fetchSql;
	}

	public void setFetchSql(String fetchSql) {
		this.fetchSql = fetchSql;
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
