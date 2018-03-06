package com.duowan.meteor.datasync;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.datasync.executor.Mysql2RedisExecutor;
import com.duowan.meteor.model.enumtype.ExecStatus;
import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.instance.InstanceTask;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.model.view.AbstractTaskDepend;
import com.duowan.meteor.model.view.importredis.ImportMysqlToRedisTask;
import com.duowan.meteor.task.TaskManager;

public class DatasyncMain {

	private static Logger logger = LoggerFactory.getLogger(DatasyncMain.class);
	private static String instanceTaskTopic = "instance_task";

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("DatasyncMain <taskId> <propFilePath> <instanceFlowId>");
			System.exit(1);
		}
		Date tdate = new Date();
		int taskId = 0;
		try {
			taskId = Integer.parseInt(args[0]);
		} catch (Exception e) {
			logger.error("", e);
			System.exit(1);
		}
		String propFilePath = args[1];
		String instanceFlowId = args[2];

		Properties props = new Properties();
		try {
			FileInputStream fis = FileUtils.openInputStream(new File(propFilePath));
			props.load(fis);
		} catch (Exception e) {
			logger.error("", e);
			System.exit(1);
		}

		StringBuilder log = new StringBuilder();
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString();
			String hostname = addr.getHostName().toString();
			log.append(hostname).append("\n");
			log.append(ip).append("\n");
		} catch (UnknownHostException e) {
			logger.error("", e);
			System.exit(1);
		}
		String cronTaskLogPath = props.getProperty("meteor.cronTaskLogPath");
		log.append(cronTaskLogPath + "/" + taskId + "\n");
		log.append(DateFormatUtils.format(tdate, "yyyyMMddHHmmss") + "\n");

		InstanceTask instanceTask = new InstanceTask();
		instanceTask.setInstanceFlowId(instanceFlowId);
		instanceTask.setReadyTime(tdate);
		instanceTask.setStartTime(tdate);

		String kafkaBrokers = props.getProperty("meteor.kafkaClusterHostPorts");
		String jdbcDriver = props.getProperty("meteor.jdbc.driver");
		String jdbcUrl = props.getProperty("meteor.jdbc.url");
		String jdbcUsername = props.getProperty("meteor.jdbc.username");
		String jdbcPassword = props.getProperty("meteor.jdbc.password");
		String tmpDataPath = cronTaskLogPath + "/" + DateFormatUtils.format(tdate, "yyyy-MM-dd") + "/" + taskId + "_" + DateFormatUtils.format(tdate, "yyyyMMddHHmmss") + "/";

		File tmpDataPathFile = null;
		try {
			tmpDataPathFile = new File(tmpDataPath);
			FileUtils.deleteQuietly(tmpDataPathFile);
			if (!tmpDataPathFile.exists()) {
				tmpDataPathFile.mkdirs();
			}
			logger.info("tmpDataPath: " + tmpDataPath);

			TaskManager taskManager = TaskManager.getInstance(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
			AbstractBase task = taskManager.getTask(taskId);
			logger.info(task.toString());
			instanceTask.setTask((AbstractTaskDepend) task);

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tdate", tdate);
			params.put("tmpDataPath", tmpDataPath);
			params.put("props", props);

			switch (FileType.getFileTypeByName(task.getFileType())) {
			case Mysql2Redis:
				Mysql2RedisExecutor.exec((ImportMysqlToRedisTask) task, params);
				break;
			default:
				break;
			}
			instanceTask.setStatus(ExecStatus.Success.name());
		} catch (Exception e) {
			logger.error("", e);
			instanceTask.setStatus(ExecStatus.Fail.name());
			log.append(ExceptionUtils.getFullStackTrace(e));
		} finally {
			FileUtils.deleteQuietly(tmpDataPathFile);
			instanceTask.setEndTime(new Date());
			instanceTask.setLog(log.toString());
			Producer<String, byte[]> producer = getKafkaProducer(kafkaBrokers);
			producer.send(new ProducerRecord<String, byte[]>(instanceTaskTopic, UUID.randomUUID().toString(), SerializationUtils.serialize(instanceTask)));
			double duration = (instanceTask.getEndTime().getTime() - instanceTask.getStartTime().getTime()) / 60000;
			logger.info("Finish! Duration of minute: " + duration);
			producer.close();
		}
	}

	public static Producer<String, byte[]> getKafkaProducer(String kafkaBrokers) {
		Properties producerProps = new Properties();
		producerProps.put("bootstrap.servers", kafkaBrokers);
		producerProps.put("acks", "1");
		producerProps.put("retries", 5);
		producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producerProps.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
		Producer<String, byte[]> producer = new KafkaProducer<>(producerProps);
		return producer;
	}
}
