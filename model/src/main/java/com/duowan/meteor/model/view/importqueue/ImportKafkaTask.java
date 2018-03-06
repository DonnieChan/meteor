package com.duowan.meteor.model.view.importqueue;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/7 0007.
 */
public class ImportKafkaTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 4268791566977956302L;
	public final String CLASS = "com.duowan.meteor.model.view.importqueue.ImportKafkaTask";

	private String brokers;
	private String topics;
	private String groupId;
	private String regTable;
	private Integer rePartitionNum = 0;

	public ImportKafkaTask() {
		this.setFileType(FileType.ImportKafka.name());
		this.setProgramClass("com.duowan.meteor.server.executor.ImportKafkaTaskExecutor");
	}
	
	@Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.brokers), "brokers cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.topics), "topics cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.groupId), "groupId cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.regTable), "regTable cannot be blank!");
	}
	
	@Override
	public void doTrim() {
		super.doTrim();
		brokers = StringUtils.trim(brokers);
		topics = StringUtils.trim(topics);
		groupId = StringUtils.trim(groupId);
		regTable = StringUtils.trim(regTable);
	}

	public String getBrokers() {
		return brokers;
	}

	public void setBrokers(String brokers) {
		this.brokers = brokers;
	}

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getRegTable() {
		return regTable;
	}

	public void setRegTable(String regTable) {
		this.regTable = regTable;
	}

	public Integer getRePartitionNum() {
		return rePartitionNum;
	}

	public void setRePartitionNum(Integer rePartitionNum) {
		this.rePartitionNum = rePartitionNum;
	}
	
}
