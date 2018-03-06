package com.duowan.meteor.transfer.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.cron.instance.AlarmDataDelayJobInstance;

public class AlarmDataDelayJob implements Job {

	protected static Logger logger = LoggerFactory.getLogger(AlarmDataDelayJob.class);

	@Override
	public void execute(JobExecutionContext context) {
		AlarmDataDelayJobInstance.getInstatnce().doAlarmDataDelay();
	}

}
