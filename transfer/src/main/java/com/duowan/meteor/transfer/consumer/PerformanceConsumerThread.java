package com.duowan.meteor.transfer.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.model.instance.InstanceDataDelay;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public class PerformanceConsumerThread extends Thread {

	private static Logger logger = LoggerFactory.getLogger(PerformanceConsumerThread.class);

	private String topic;
	private Integer numThreads;
	private Properties consumerProps = new Properties();
	private String[] parsePatterns = new String[] {"yyyy-MM-dd HH:mm:ss"};
	private boolean isRunning = true;

	public PerformanceConsumerThread(String host, String groupId, String topic, Integer numThreads) {
		this.topic = topic;
		this.numThreads = numThreads;
		consumerProps.put("bootstrap.servers", host);
		consumerProps.put("group.id", groupId);
		consumerProps.put("enable.auto.commit", "true");
		consumerProps.put("auto.commit.interval.ms", 1000);
		consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
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
				private KafkaConsumer<String, String> consumer = null;

				@Override
				public void run() {
					String threadName = "performanceConsumer" + n;
					Thread.currentThread().setName(threadName);
					consumer  = new KafkaConsumer<String, String>(consumerProps);
					consumer.subscribe(Arrays.asList(topic));
					while (isRunning) {
						try {
							ConsumerRecords<String, String> records = consumer.poll(3000);
							if (records == null || records.isEmpty()) {
								continue;
							}
							Map<String, InstanceDataDelay> instanceDataDelayMap = new HashMap<String, InstanceDataDelay>();
							for (ConsumerRecord<String, String> record : records) {
								String[] msgSplitArr = StringUtils.split(record.value(), "|");
								int taskId = Integer.parseInt(msgSplitArr[0]);
								int delayMillis = Integer.parseInt(msgSplitArr[1]);
								int sourceTaskId = Integer.parseInt(msgSplitArr[2]);
								String curTime = msgSplitArr[3];
								String key = taskId + "|" + curTime + "|" + sourceTaskId;
								InstanceDataDelay instanceDataDelay = instanceDataDelayMap.get(key);
								if (instanceDataDelay == null || delayMillis > instanceDataDelay.getDelayMillis()) {
									instanceDataDelay = new InstanceDataDelay();
									instanceDataDelay.setSourceTaskId(sourceTaskId);
									instanceDataDelay.setTaskId(taskId);
									instanceDataDelay.setDelayMillis(delayMillis);
									instanceDataDelay.setTtime(DateUtils.parseDate(curTime, parsePatterns));
									instanceDataDelayMap.put(key, instanceDataDelay);
								}
							}
							List<InstanceDataDelay> instanceDataDelayList = new ArrayList<InstanceDataDelay>(instanceDataDelayMap.values());
							ServiceConfigTool.instanceDataDelayService.batchInsert(instanceDataDelayList);
							logger.info(threadName + ": " + instanceDataDelayList.size());
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
