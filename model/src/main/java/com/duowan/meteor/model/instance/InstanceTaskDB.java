package com.duowan.meteor.model.instance;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class InstanceTaskDB implements java.io.Serializable {

	private static final long serialVersionUID = 7929717506075356046L;

	private String instanceFlowId;

	private java.lang.Integer fileId;
	
	private java.lang.String fileName;

	private java.lang.String fileBody;

	private java.util.Date readyTime;

	private java.util.Date startTime;

	private java.util.Date endTime;

	private java.lang.String status;

	private java.lang.Integer retriedTimes = 0;

	private java.lang.String log;

	private Integer poolActiveCount = 0;

	private Integer poolQueueSize = 0;

	private java.lang.String remarks;

	private Integer isValid = 1;

	private java.util.Date createTime = new Date();

	private java.util.Date updateTime = new Date();

	private java.lang.String createUser = "sys";

	private java.lang.String updateUser = "sys";

	public String getInstanceFlowId() {
		return instanceFlowId;
	}

	public void setInstanceFlowId(String instanceFlowId) {
		this.instanceFlowId = instanceFlowId;
	}

	public java.lang.Integer getFileId() {
		return fileId;
	}

	public void setFileId(java.lang.Integer fileId) {
		this.fileId = fileId;
	}

	public java.lang.String getFileBody() {
		return fileBody;
	}

	public void setFileBody(java.lang.String fileBody) {
		this.fileBody = fileBody;
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

	public java.lang.String getRemarks() {
		return remarks;
	}

	public void setRemarks(java.lang.String remarks) {
		this.remarks = remarks;
	}

	public Integer getIsValid() {
		return isValid;
	}

	public void setIsValid(Integer isValid) {
		this.isValid = isValid;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public java.util.Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(java.util.Date updateTime) {
		this.updateTime = updateTime;
	}

	public java.lang.String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(java.lang.String createUser) {
		this.createUser = createUser;
	}

	public java.lang.String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(java.lang.String updateUser) {
		this.updateUser = updateUser;
	}

	public java.lang.String getFileName() {
		return fileName;
	}

	public void setFileName(java.lang.String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
