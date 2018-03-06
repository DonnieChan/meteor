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

import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.instance.InstanceTask;
import com.duowan.meteor.model.instance.InstanceTaskDB;
import com.duowan.meteor.model.util.ViewDBConverters;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public class InstanceTaskConsumerThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(InstanceTaskConsumerThread.class);

	private String topic;
	private Integer numThreads;
	private Properties consumerProps = new Properties();
	private boolean isRunning = true;

	public InstanceTaskConsumerThread(String host, String groupId, String topic, Integer numThreads) {
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
					String threadName = "taskConsumer" + n;
					Thread.currentThread().setName(threadName);
					consumer  = new KafkaConsumer<String, byte[]>(consumerProps);
					consumer.subscribe(Arrays.asList(topic));
					while (isRunning) {
						try {
							ConsumerRecords<String, byte[]> records = consumer.poll(3000);
							if (records == null || records.isEmpty()) {
								continue;
							}
							List<InstanceTaskDB> instanceTaskDBList = new ArrayList<InstanceTaskDB>();
							for (ConsumerRecord<String, byte[]> record : records) {
								InstanceTask instance = (InstanceTask) SerializationUtils.deserialize(record.value());
								InstanceTaskDB instanceDB = new InstanceTaskDB();
								instanceDB.setInstanceFlowId(instance.getInstanceFlowId());
								DefFileSys defFileSys = null;
								try {
									defFileSys = ViewDBConverters.convertToDefFileSys(instance.getTask());
								} catch (Exception e) {
									logger.error("", e);
								}
								instanceDB.setFileId(defFileSys.getFileId());
								instanceDB.setFileBody(defFileSys.getFileBody());
								instanceDB.setReadyTime(instance.getReadyTime());
								instanceDB.setStartTime(instance.getStartTime());
								instanceDB.setEndTime(instance.getEndTime());
								instanceDB.setStatus(instance.getStatus());
								instanceDB.setRetriedTimes(instance.getRetriedTimes());
								instanceDB.setLog(instance.getLog());
								instanceDB.setPoolActiveCount(instance.getPoolActiveCount());
								instanceDB.setPoolQueueSize(instance.getPoolQueueSize());
								instanceDB.setUpdateTime(defFileSys.getUpdateTime());
								instanceDB.setUpdateUser(defFileSys.getUpdateUser());
								instanceTaskDBList.add(instanceDB);
							}
							ServiceConfigTool.instanceTaskService.batchInsert(instanceTaskDBList);
							logger.info(threadName + ": " + instanceTaskDBList.size());
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
