package com.duowan.meteor.transfer.tool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.duowan.meteor.dao.common.ApplicationContextHolder;
import com.duowan.meteor.service.AlarmDataDelayService;
import com.duowan.meteor.service.AlarmErrorService;
import com.duowan.meteor.service.AlarmLoopCompareService;
import com.duowan.meteor.service.AlarmSliceDelayService;
import com.duowan.meteor.service.InstanceDataDelayService;
import com.duowan.meteor.service.InstanceFlowService;
import com.duowan.meteor.service.InstanceTaskService;
import com.duowan.meteor.service.TaskService;

public class ServiceConfigTool {

	private static Logger logger = LoggerFactory.getLogger(ServiceConfigTool.class);

	public static ExecutorService threadExecutor = Executors.newCachedThreadPool();

	public static String kafkaConnectHost = "";

	public static String kafkaFlowTopic = "instance_flow";
	public static int kafkaFlowThreadNum = 5;
	public static String flowKafkaGroupId = "meteorTransfer_flow";

	public static String kafkaTaskTopic = "instance_task";
	public static int kafkaTaskThreadNum = 5;
	public static String taskKafkaGroupId = "meteorTransfer_task";

	public static String kafkaPerformanceTopic = "performance";
	public static int kafakPerformanceThreadNum = 5;
	public static String performanceKafkaGroupId = "meteorTransfer_performance";

	public static InstanceFlowService instanceFlowService;
	public static InstanceTaskService instanceTaskService;
	public static SimpleJdbcTemplate simpleJdbcTemplate;
	public static InstanceDataDelayService instanceDataDelayService;
	public static AlarmDataDelayService alarmDataDelayService;
	public static AlarmErrorService alarmErrorService;
	public static AlarmLoopCompareService alarmLoopCompareService;
	public static AlarmSliceDelayService alarmSliceDelayService;
	public static TaskService taskService;

	public static void startup() throws IOException {
		logger.info("ServiceConfigTool startup");
		kafkaConnectHost = System.getProperty("kafkaConnectHost");
		instanceFlowService = (InstanceFlowService) ApplicationContextHolder.getBean("instanceFlowService");
		instanceTaskService = (InstanceTaskService) ApplicationContextHolder.getBean("instanceTaskService");
		simpleJdbcTemplate = (SimpleJdbcTemplate) ApplicationContextHolder.getBean("simpleJdbcTemplate");
		instanceDataDelayService = (InstanceDataDelayService) ApplicationContextHolder.getBean("instanceDataDelayService");
		alarmDataDelayService = (AlarmDataDelayService) ApplicationContextHolder.getBean("alarmDataDelayService");
		alarmErrorService = (AlarmErrorService) ApplicationContextHolder.getBean("alarmErrorService");
		alarmLoopCompareService = (AlarmLoopCompareService) ApplicationContextHolder.getBean("alarmLoopCompareService");
		alarmSliceDelayService = (AlarmSliceDelayService) ApplicationContextHolder.getBean("alarmSliceDelayService");
		taskService = (TaskService) ApplicationContextHolder.getBean("taskService");
	}
}
