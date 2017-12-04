package com.duowan.meteor.model.instance;

import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class InstanceFlow implements java.io.Serializable {

	private static final long serialVersionUID = -8127075944463746696L;
	private String instanceFlowId;
	private Date initTime;
	private Integer sourceTaskId;
	
	private Date startTime;
	private Date endTime;
	private String status;
	private String log;
	
	private java.lang.String remarks;
	private java.lang.Integer isValid = 1;
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
	public Date getInitTime() {
		return initTime;
	}
	public void setInitTime(Date initTime) {
		this.initTime = initTime;
	}
	public Integer getSourceTaskId() {
		return sourceTaskId;
	}
	public void setSourceTaskId(Integer sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public java.lang.String getRemarks() {
		return remarks;
	}
	public void setRemarks(java.lang.String remarks) {
		this.remarks = remarks;
	}
	public java.lang.Integer getIsValid() {
		return isValid;
	}
	public void setIsValid(java.lang.Integer isValid) {
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
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
