package com.duowan.meteor.transfer.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.cron.instance.AlarmLoopCompareJobInstance;


public class AlarmLoopCompareJob implements Job {
	
	protected static Logger logger = LoggerFactory.getLogger(AlarmLoopCompareJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		AlarmLoopCompareJobInstance.getInstatnce().doAlarmLoopCompareJob();
	}
	
}
