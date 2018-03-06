package com.duowan.meteor.transfer.cron;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.tool.ServiceConfigTool;

/**
 * 清理任务
 * @author chenwu
 */
public class CleanJob implements Job {
	
	private static Logger logger = LoggerFactory.getLogger(CleanJob.class);
	
	@Override
	public void execute(JobExecutionContext context) {
		logger.info("CleanJob");
		try {
			cleanHistroyData();
		} catch(Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 清理历史数据
	 */
	private void cleanHistroyData() {
		ServiceConfigTool.instanceFlowService.cleanHistory(DateUtils.addDays(new Date(), -120));
		
		ServiceConfigTool.instanceTaskService.cleanHistory(DateUtils.addDays(new Date(), -120));
		
		ServiceConfigTool.instanceDataDelayService.cleanHistory(DateUtils.addDays(new Date(), -120));
	}
	
}
