package com.duowan.meteor.transfer.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.SerializationUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public class InstanceFlowConsumerThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(InstanceFlowConsumerThread.class);

	private String topic;
	private Integer numThreads;
	private Properties consumerProps = new Properties();
	private boolean isRunning = true;

	public InstanceFlowConsumerThread(String host, String groupId, String topic, Integer numThreads) {
		this.topic = topic;
		this.numThreads = numThreads;
		consumerProps.put("bootstrap.servers", host);
		consumerProps.put("group.id", groupId);
		consumerProps.put("enable.auto.commit", "true");
		consumerProps.put("auto.commit.interval.ms", 1000);
		consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
		consumerProps.put("max.partition.fetch.bytes", 52428800);
		consumerProps.put("max.poll.records", 1000);
		consumerProps.put("receive.buffer.bytes", 1048576);
		consumerProps.put("auto.offset.reset", "latest");

	}

	@Override
	public void run() {
		for (int i=0; i<numThreads; i++) {
			final int n = i;
			ServiceConfigTool.threadExecutor.submit(new Runnable() {
				private KafkaConsumer<String, byte[]> consumer = null;

				@Override
				public void run() {
					String threadName = "flowConsumer" + n;
					Thread.currentThread().setName(threadName);
					consumer  = new KafkaConsumer<String, byte[]>(consumerProps);
					consumer.subscribe(Arrays.asList(topic));
					while (isRunning) {
						try {
							ConsumerRecords<String, byte[]> records = consumer.poll(3000);
							if (records == null || records.isEmpty()) {
								continue;
							}
							List<InstanceFlow> instanceFlowList = new ArrayList<InstanceFlow>();
							for (ConsumerRecord<String, byte[]> record : records) {
								InstanceFlow instance = (InstanceFlow) SerializationUtils.deserialize(record.value());
								instanceFlowList.add(instance);
							}
							ServiceConfigTool.instanceFlowService.batchInsert(instanceFlowList);
							logger.info(threadName + ": " + instanceFlowList.size());
						} catch (Exception e) {
							logger.error("", e);
						}
					}
				}
			});
		}
	}
	
	public void shutdown() {
		isRunning = false;
	}
}
