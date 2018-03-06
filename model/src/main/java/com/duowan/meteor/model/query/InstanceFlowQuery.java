package com.duowan.meteor.model.query;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * tableName: instance_flow [InstanceFlow] 
 * @author liuchaohong
 */
public class InstanceFlowQuery  implements java.io.Serializable{
	
	private static final long serialVersionUID = 5454155825314635342L;

	private java.lang.String instanceFlowId;
	
	private java.lang.Integer sourceTaskId;

	private java.lang.String startTime;

	private java.lang.String endTime;
	
	private java.lang.String status;
	
	private java.lang.String remarks;

	private java.lang.Integer isValid;
	
	private java.util.Date createTime;

	private java.util.Date updateTime;

	private java.lang.String createUser;

	private java.lang.String updateUser;

	private java.lang.Integer firstIndex;
	
	private java.lang.Integer pageCount;
	

	public java.lang.String getStartTime() {
		return startTime;
	}

	public void setStartTime(java.lang.String startTime) {
		this.startTime = startTime;
	}

	public java.lang.String getEndTime() {
		return endTime;
	}

	public void setEndTime(java.lang.String endTime) {
		this.endTime = endTime;
	}

	public java.lang.String getStatus() {
		return status;
	}

	public void setStatus(java.lang.String status) {
		this.status = status;
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

	public java.lang.Integer getFirstIndex() {
		return firstIndex;
	}

	public void setFirstIndex(java.lang.Integer firstIndex) {
		this.firstIndex = firstIndex;
	}

	public java.lang.Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(java.lang.Integer pageCount) {
		this.pageCount = pageCount;
	}

	public java.lang.String getInstanceFlowId() {
		return instanceFlowId;
	}

	public void setInstanceFlowId(java.lang.String instanceFlowId) {
		this.instanceFlowId = instanceFlowId;
	}

	public Integer getSourceTaskId() {
		return sourceTaskId;
	}

	public void setSourceTaskId(Integer sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	
	
	
	
}

