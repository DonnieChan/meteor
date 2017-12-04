package com.duowan.meteor.model.instance;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.duowan.meteor.model.view.AbstractTaskDepend;

public class InstanceTask implements java.io.Serializable {

	private static final long serialVersionUID = 7929717506075356046L;

	private String instanceFlowId;
	
	private java.util.Date readyTime;
	
	private java.util.Date startTime;

	private java.util.Date endTime;
	
	private java.lang.String status;

	private java.lang.Integer retriedTimes = 0;

	private java.lang.String log;
	
	private Integer poolActiveCount = 0;
	
	private Integer poolQueueSize = 0;
	
	private AbstractTaskDepend task;

	public String getInstanceFlowId() {
		return instanceFlowId;
	}

	public void setInstanceFlowId(String instanceFlowId) {
		this.instanceFlowId = instanceFlowId;
	}

	public java.util.Date getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(java.util.Date readyTime) {
		this.readyTime = readyTime;
	}

	public java.util.Date getStartTime() {
		return startTime;
	}

	public void setStartTime(java.util.Date startTime) {
		this.startTime = startTime;
	}

	public java.util.Date getEndTime() {
		return endTime;
	}

	public void setEndTime(java.util.Date endTime) {
		this.endTime = endTime;
	}

	public java.lang.String getStatus() {
		return status;
	}

	public void setStatus(java.lang.String status) {
		this.status = status;
	}

	public java.lang.Integer getRetriedTimes() {
		return retriedTimes;
	}

	public void setRetriedTimes(java.lang.Integer retriedTimes) {
		this.retriedTimes = retriedTimes;
	}

	public java.lang.String getLog() {
		return log;
	}

	public void setLog(java.lang.String log) {
		this.log = log;
	}
	
	public AbstractTaskDepend getTask() {
		return task;
	}

	public void setTask(AbstractTaskDepend task) {
		this.task = task;
	}

	public Integer getPoolActiveCount() {
		return poolActiveCount;
	}

	public void setPoolActiveCount(Integer poolActiveCount) {
		this.poolActiveCount = poolActiveCount;
	}

	public Integer getPoolQueueSize() {
		return poolQueueSize;
	}

	public void setPoolQueueSize(Integer poolQueueSize) {
		this.poolQueueSize = poolQueueSize;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
