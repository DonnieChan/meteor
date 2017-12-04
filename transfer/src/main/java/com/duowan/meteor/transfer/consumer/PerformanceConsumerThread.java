package com.duowan.meteor.transfer.consumer;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public class PerformanceConsumerThread extends Thread {

	protected static Logger logger = LoggerFactory.getLogger(PerformanceConsumerThread.class);

	private ConsumerConnector consumerConnector;
	private String topic;
	private Integer numThreads;
	private boolean runFlag = true;

	public PerformanceConsumerThread(String host, String groupId, String topic, Integer numThreads) {
		consumerConnector = kafka.consumer.Consumer.createJavaConsumerConnector(createConsumerConfig(host, groupId));
		this.topic = topic;
		this.numThreads = numThreads;
	}

	@Override
	public void run() {
		try {
			Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
			topicCountMap.put(topic, numThreads);
			Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicCountMap);
			List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

			for (final KafkaStream<byte[], byte[]> stream : streams) {
				ServiceConfigTool.threadExecutor.submit(new Runnable() {
					@Override
					public void run() {
						ConsumerIterator<byte[], byte[]> it = stream.iterator();
						while (runFlag && it.hasNext()) {
							String msg = "";
							try {
								msg = new String(it.next().message(), "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							String[] msgSplitArr = StringUtils.split(msg, "|");
							logger.debug(msgSplitArr.toString());
							if (StringUtils.equals(ServiceConfigTool.dwEnv, "prod")) {
								// TODO 监控报警
							}
						}
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		logger.info("shutdown consumerConnector");
		runFlag = false;
		if (consumerConnector != null) {
			consumerConnector.shutdown();
		}
	}

	private ConsumerConfig createConsumerConfig(String host, String groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", host);
		props.put("group.id", groupId);
		props.put("zookeeper.session.timeout.ms", "10000");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		return new ConsumerConfig(props);
	}
}
