package com.duowan.meteor.transfer.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.cron.instance.AlarmErrorJobInstance;


public class AlarmErrorJob implements Job {
	
	protected static Logger logger = LoggerFactory.getLogger(AlarmErrorJob.class);
	
	@Override
	public void execute(JobExecutionContext context) {
		AlarmErrorJobInstance.getInstatnce().doAlarmError();
	}
	
}
