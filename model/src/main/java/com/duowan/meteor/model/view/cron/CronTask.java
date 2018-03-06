package com.duowan.meteor.model.view.cron;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/7 0007.
 */
public class CronTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 4059981124235687625L;
	public final String CLASS = "com.duowan.meteor.model.view.cron.CronTask";
    
    private String cronExp;

    public CronTask() {
        this.setFileType(FileType.Cron.name());
        this.setProgramClass("com.duowan.meteor.server.executor.CronTaskExecutor");
    }
    
    @Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.cronExp), "cronExp cannot be blank!");
	}
    
    @Override
    public void doTrim() {
    	super.doTrim();
    	cronExp = StringUtils.trim(cronExp);
    }

	public String getCronExp() {
		return cronExp;
	}

	public void setCronExp(String cronExp) {
		this.cronExp = cronExp;
	}


}
