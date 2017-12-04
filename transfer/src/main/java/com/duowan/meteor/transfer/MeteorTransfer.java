package com.duowan.meteor.transfer;

import com.duowan.meteor.transfer.consumer.InstanceConsumerThread;
import com.duowan.meteor.transfer.consumer.InstanceFlowAction;
import com.duowan.meteor.transfer.consumer.InstanceTaskAction;
import com.duowan.meteor.transfer.consumer.PerformanceConsumerThread;
import com.duowan.meteor.transfer.cron.QuartzCluster;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public class MeteorTransfer {

	private static InstanceConsumerThread flowThread = null;
	private static InstanceConsumerThread taskThread = null;
	private static PerformanceConsumerThread performanceConsumerThread = null;

	public static void startup() throws Exception {
		ServiceConfigTool.startup();
		QuartzCluster.startup();

		flowThread = new InstanceConsumerThread(ServiceConfigTool.kafkaConnectHost, ServiceConfigTool.kafkaGroupId, ServiceConfigTool.kafkaFlowTopic, ServiceConfigTool.kafkaFlowThreadNum,
				new InstanceFlowAction(ServiceConfigTool.kafkaFlowBatchMaxSize, ServiceConfigTool.kafkaFlowBatchIntervalMilli));
		flowThread.start();

		taskThread = new InstanceConsumerThread(ServiceConfigTool.kafkaConnectHost, ServiceConfigTool.kafkaGroupId, ServiceConfigTool.kafkaTaskTopic, ServiceConfigTool.kafkaTaskThreadNum,
				new InstanceTaskAction(ServiceConfigTool.kafkaFlowBatchMaxSize, ServiceConfigTool.kafkaFlowBatchIntervalMilli));
		taskThread.start();

		performanceConsumerThread = new PerformanceConsumerThread(ServiceConfigTool.kafkaConnectHost, ServiceConfigTool.kafkaGroupId, ServiceConfigTool.kafkaPerformanceTopic,
				ServiceConfigTool.kafakPerformanceThreadNum);
		performanceConsumerThread.start();
	}

	public static void endup() throws Exception {
		if (flowThread != null) {
			flowThread.shutdown();
		}
		if (taskThread != null) {
			taskThread.shutdown();
		}
		if (performanceConsumerThread != null) {
			performanceConsumerThread.shutdown();
		}
		Thread.sleep(60000l);
	}
}
