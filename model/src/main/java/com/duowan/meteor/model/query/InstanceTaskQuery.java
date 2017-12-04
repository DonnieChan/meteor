package com.duowan.meteor.model.query;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * tableName: instance_task [InstanceTask] 
 * @author liuchaohong
 */
public class InstanceTaskQuery  implements java.io.Serializable{
	
	private static final long serialVersionUID = 5454155825314635342L;

	private java.lang.String instanceFlowId;

	private java.lang.Integer fileId;
	
	private java.lang.Integer parentFileId;
	
	private Integer sourceFileId;
	
	private java.lang.Integer projectId;

	private java.lang.String fileName;

	private java.lang.String fileType;
	
	private java.lang.String fileBody;
	
	private java.lang.String fileProps;
	
	private java.lang.String preDepends;

	private java.util.Date offlineTime;
	
	private java.lang.String contacts;
	
	private java.util.Date readyTime;
	
	private java.lang.String startTime;

	private java.lang.String endTime;
	
	private java.lang.String status;

	private java.lang.Integer retriedTimes;
	
	private java.lang.String runMachine;

	private java.lang.String log;
	
	private java.lang.String remarks;

	private java.lang.Integer isValid;
	
	private java.lang.Integer isIgnoreError;
	
	private java.util.Date createTime;

	private java.util.Date updateTime;

	private java.lang.String createUser;

	private java.lang.String updateUser;

	private java.lang.Integer firstIndex;
	
	private java.lang.Integer pageCount;
	
	

	public java.lang.String getInstanceFlowId() {
		return instanceFlowId;
	}

	public void setInstanceFlowId(java.lang.String instanceFlowId) {
		this.instanceFlowId = instanceFlowId;
	}

	public java.lang.Integer getFileId() {
		return fileId;
	}

	public void setFileId(java.lang.Integer fileId) {
		this.fileId = fileId;
	}

	public java.lang.Integer getParentFileId() {
		return parentFileId;
	}

	public void setParentFileId(java.lang.Integer parentFileId) {
		this.parentFileId = parentFileId;
	}

	public java.lang.Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	public java.lang.String getFileName() {
		return fileName;
	}

	public void setFileName(java.lang.String fileName) {
		this.fileName = fileName;
	}

	public java.lang.String getFileType() {
		return fileType;
	}

	public void setFileType(java.lang.String fileType) {
		this.fileType = fileType;
	}

	public java.lang.String getFileBody() {
		return fileBody;
	}

	public void setFileBody(java.lang.String fileBody) {
		this.fileBody = fileBody;
	}

	public java.lang.String getFileProps() {
		return fileProps;
	}

	public void setFileProps(java.lang.String fileProps) {
		this.fileProps = fileProps;
	}

	public java.lang.String getPreDepends() {
		return preDepends;
	}

	public void setPreDepends(java.lang.String preDepends) {
		this.preDepends = preDepends;
	}

	public java.util.Date getOfflineTime() {
		return offlineTime;
	}

	public void setOfflineTime(java.util.Date offlineTime) {
		this.offlineTime = offlineTime;
	}

	public java.lang.String getContacts() {
		return contacts;
	}

	public void setContacts(java.lang.String contacts) {
		this.contacts = contacts;
	}

	public java.util.Date getReadyTime() {
		return readyTime;
	}

	public void setReadyTime(java.util.Date readyTime) {
		this.readyTime = readyTime;
	}

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

	public java.lang.Integer getRetriedTimes() {
		return retriedTimes;
	}

	public void setRetriedTimes(java.lang.Integer retriedTimes) {
		this.retriedTimes = retriedTimes;
	}

	public java.lang.String getRunMachine() {
		return runMachine;
	}

	public void setRunMachine(java.lang.String runMachine) {
		this.runMachine = runMachine;
	}

	public java.lang.String getLog() {
		return log;
	}

	public void setLog(java.lang.String log) {
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

	public java.lang.Integer getIsIgnoreError() {
		return isIgnoreError;
	}

	public void setIsIgnoreError(java.lang.Integer isIgnoreError) {
		this.isIgnoreError = isIgnoreError;
	}
	
	/**
	 * @return the sourceFileId
	 */
	public Integer getSourceFileId() {
		return sourceFileId;
	}

	/**
	 * @param sourceFileId the sourceFileId to set
	 */
	public void setSourceFileId(Integer sourceFileId) {
		this.sourceFileId = sourceFileId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}	
	
	
}

