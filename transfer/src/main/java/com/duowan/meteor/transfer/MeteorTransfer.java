package com.duowan.meteor.transfer;

import com.duowan.meteor.transfer.consumer.InstanceFlowConsumerThread;
import com.duowan.meteor.transfer.consumer.InstanceTaskConsumerThread;
import com.duowan.meteor.transfer.consumer.PerformanceConsumerThread;
import com.duowan.meteor.transfer.cron.QuartzCluster;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public class MeteorTransfer {

	private static InstanceFlowConsumerThread flowThread = null;
	private static InstanceTaskConsumerThread taskThread = null;
	private static PerformanceConsumerThread performanceConsumerThread = null;

	public static void startup() throws Exception {
		ServiceConfigTool.startup();
		QuartzCluster.startup();

		flowThread = new InstanceFlowConsumerThread(ServiceConfigTool.kafkaConnectHost, ServiceConfigTool.flowKafkaGroupId, ServiceConfigTool.kafkaFlowTopic, ServiceConfigTool.kafkaFlowThreadNum);
		flowThread.start();

		taskThread = new InstanceTaskConsumerThread(ServiceConfigTool.kafkaConnectHost, ServiceConfigTool.taskKafkaGroupId, ServiceConfigTool.kafkaTaskTopic, ServiceConfigTool.kafkaTaskThreadNum);
		taskThread.start();

		performanceConsumerThread = new PerformanceConsumerThread(ServiceConfigTool.kafkaConnectHost, ServiceConfigTool.performanceKafkaGroupId, ServiceConfigTool.kafkaPerformanceTopic,
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
