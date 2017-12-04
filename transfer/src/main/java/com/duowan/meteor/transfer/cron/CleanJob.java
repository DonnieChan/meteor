package com.duowan.meteor.transfer.cron;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
	public void execute(JobExecutionContext context) throws JobExecutionException {
		logger.info("CleanJob");
		cleanHistroyData();
	}

	/**
	 * 清理历史数据
	 * 执行OPTIMIZE TABLE xx, 是为了应用MyIsam的concurrent insert特性：读和写可以并发
	 */
	private void cleanHistroyData() {
		ServiceConfigTool.instanceFlowService.cleanHistory(DateUtils.addDays(new Date(), -2));
		ServiceConfigTool.simpleJdbcTemplate.getJdbcOperations().execute("OPTIMIZE TABLE instance_flow");
		
		ServiceConfigTool.instanceTaskService.cleanHistory(DateUtils.addDays(new Date(), -2));
		ServiceConfigTool.simpleJdbcTemplate.getJdbcOperations().execute("OPTIMIZE TABLE instance_task");
	}
	
}
