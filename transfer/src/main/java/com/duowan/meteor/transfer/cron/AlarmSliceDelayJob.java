package com.duowan.meteor.transfer.cron;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.cron.instance.AlarmSliceDelayJobInstance;

public class AlarmSliceDelayJob implements Job {

	protected static Logger logger = LoggerFactory.getLogger(AlarmSliceDelayJob.class);

	@Override
	public void execute(JobExecutionContext context) {
		AlarmSliceDelayJobInstance.getInstatnce().doAlarmSliceDelay();
	}

}
