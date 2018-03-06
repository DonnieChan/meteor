package com.duowan.meteor.transfer.cron;

import java.io.IOException;
import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.dao.common.ApplicationContextHolder;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 实时器，quartz集群，用于确保应用发布至多台机器做负载均衡，同一时刻只有一台机器执行某个任务
 * @author chenwu
 */
public class QuartzCluster {

	private static Logger logger = LoggerFactory.getLogger(QuartzCluster.class);
	
	private static SchedulerFactory factory;
	private static Scheduler scheduler;

	/**
	 * 定时器初始化启动
	 * @throws SchedulerException
	 * @throws IOException 
	 */
	public static void startup() throws SchedulerException, IOException {
		Properties props = loadQuartzClusterConfig();
		logger.info("QuartzCluster props : " + props);
		
		factory = new StdSchedulerFactory(props);
		scheduler = factory.getScheduler();
		scheduler.start();
		addJobIfNotExists();
	}

	/**
	 * 定时任务，初始化进quartz集群数据库
	 * @throws SchedulerException 
	 */
	private static void addJobIfNotExists() throws SchedulerException {
		addJobIfNotExists(CleanJob.class, "CleanJob", "0 0 6 * * ?");
		addJobIfNotExists(AlarmErrorJob.class, "AlarmErrorJob", "0 0/1 * * * ?");
		addJobIfNotExists(AlarmSliceDelayJob.class, "AlarmSliceDelayJob", "0 0/1 * * * ?");
		addJobIfNotExists(AlarmDataDelayJob.class, "AlarmDataDelayJob", "0 0/1 * * * ?");
		addJobIfNotExists(AlarmLoopCompareJob.class, "AlarmLoopCompareJob", "0 0/1 * * * ?");
	}
	
	/**
	 * 添加定时任务
	 * @throws SchedulerException 
	 */
	private static void addJobIfNotExists(Class<? extends Job> jobClass, String jobName, String cronExp) throws SchedulerException {
		JobKey jobKey = new JobKey(jobName, Scheduler.DEFAULT_GROUP);
		if(!scheduler.checkExists(jobKey)) {
			addFlowTrigger(jobClass, jobName, Scheduler.DEFAULT_GROUP, jobName, Scheduler.DEFAULT_GROUP, cronExp);
		}
	}

	/**
	 * 加载quartz集群配置
	 * @return
	 * @throws IOException
	 */
	private static Properties loadQuartzClusterConfig() throws IOException {
		Properties props = new Properties();
		props.load(QuartzCluster.class.getResourceAsStream("/quartz.properties"));
		//数据源配置文件，因升龙加密，可通过如下方式取得
		ComboPooledDataSource quartzClusterDS = (ComboPooledDataSource) ApplicationContextHolder.getBean("quartzClusterDS");
		props.put("org.quartz.dataSource.myDS.URL", quartzClusterDS.getJdbcUrl());
		props.put("org.quartz.dataSource.myDS.user", quartzClusterDS.getUser());
		props.put("org.quartz.dataSource.myDS.password", quartzClusterDS.getPassword());
		return props;
	}
	
	/**
	 * 添加定时任务
	 * @param jobClass
	 * @param jobName
	 * @param jobGroup
	 * @param cronTriggerName
	 * @param cronTriggerGroup
	 * @param cronExp
	 * @throws SchedulerException
	 */
	public static void addFlowTrigger(Class<? extends Job> jobClass, String jobName, String jobGroup, String cronTriggerName, String cronTriggerGroup, String cronExp) throws SchedulerException {
		JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup).build();
		CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(cronTriggerName, cronTriggerGroup).withSchedule(CronScheduleBuilder.cronSchedule(cronExp)).build();
		scheduler.scheduleJob(job, trigger);
	}
}
