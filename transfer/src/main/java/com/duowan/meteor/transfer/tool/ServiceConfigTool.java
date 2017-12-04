package com.duowan.meteor.transfer.tool;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.duowan.meteor.dao.common.ApplicationContextHolder;
import com.duowan.meteor.service.InstanceFlowService;
import com.duowan.meteor.service.InstanceTaskService;

public class ServiceConfigTool {

	private static Logger logger = LoggerFactory.getLogger(ServiceConfigTool.class);
	
	public static ExecutorService threadExecutor = Executors.newCachedThreadPool();
	
	public static String dwEnv = "prod";
	
	public static String kafkaConnectHost = "cassandra1:2181,cassandra2:2181,cassandra3:2181";
	public static String kafkaGroupId = "meteorTransfer";
	
	public static String kafkaFlowTopic = "instance_flow";
	public static int kafkaFlowThreadNum = 5;
	public static int kafkaFlowBatchMaxSize = 3;
	public static long kafkaFlowBatchIntervalMilli = 15000l;

	public static String kafkaTaskTopic = "instance_task";
	public static int kafkaTaskThreadNum = 5;
	public static int kafkaTaskBatchMaxSize = 30;
	public static long kafkaTaskBatchIntervalMilli = 15000l;
	
	public static String kafkaPerformanceTopic = "performance";
	public static int kafakPerformanceThreadNum = 5;
	
	public static InstanceFlowService instanceFlowService;
	public static InstanceTaskService instanceTaskService;
	public static SimpleJdbcTemplate simpleJdbcTemplate;
	
	public static void startup() throws IOException {
		logger.info("ServiceConfigTool startup");
		instanceFlowService = (InstanceFlowService) ApplicationContextHolder.getBean("instanceFlowService");
		instanceTaskService = (InstanceTaskService) ApplicationContextHolder.getBean("instanceTaskService");
		simpleJdbcTemplate = (SimpleJdbcTemplate) ApplicationContextHolder.getBean("simpleJdbcTemplate");
	}
}
