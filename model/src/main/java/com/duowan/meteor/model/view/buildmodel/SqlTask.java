package com.duowan.meteor.model.view.buildmodel;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/7 0007.
 */
public class SqlTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 4059981124235687625L;
	public final String CLASS = "com.duowan.meteor.model.view.buildmodel.SqlTask";
    
    private String sql;
    private Integer repartition = 0;

    public SqlTask() {
        this.setFileType(FileType.SqlTask.name());
        this.setProgramClass("com.duowan.meteor.server.executor.SqlTaskExecutor");
    }
    
    @Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.sql), "sql cannot be blank!");
	}
    
    @Override
    public void doTrim() {
    	super.doTrim();
    	sql = StringUtils.trim(sql);
    }

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Integer getRepartition() {
		return repartition;
	}

	public void setRepartition(Integer repartition) {
		this.repartition = repartition;
	}
	
}
