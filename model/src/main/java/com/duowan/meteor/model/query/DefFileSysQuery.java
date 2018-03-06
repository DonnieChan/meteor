package com.duowan.meteor.model.query;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * @author chenwu
 */
public class DefFileSysQuery {

	private java.lang.Integer fileId;

	private java.lang.Integer parentFileId;

	private java.lang.Integer projectId;

	private java.lang.String fileName;

	private java.lang.String fileType;

	private java.lang.String fileBody;

	private Integer isDir;

	private java.util.Date offlineTimeBegin;
	private java.util.Date offlineTimeEnd;
	
	private java.lang.String contacts;

	private java.lang.String remarks;

	private Integer isValid;

	private java.util.Date createTimeBegin;
	private java.util.Date createTimeEnd;

	private java.util.Date updateTimeBegin;
	private java.util.Date updateTimeEnd;

	private java.lang.String createUser;

	private java.lang.String updateUser;

	private java.lang.String lineStatus;
	
	public DefFileSysQuery(){
		
	}
	
	public DefFileSysQuery(String fileType) {
		this.fileType = fileType;
	}

	public java.lang.Integer getFileId() {
		return this.fileId;
	}
	
	public void setFileId(java.lang.Integer value) {
		this.fileId = value;
	}
	
	public java.lang.Integer getParentFileId() {
		return this.parentFileId;
	}
	
	public void setParentFileId(java.lang.Integer value) {
		this.parentFileId = value;
	}
	
	public java.lang.Integer getProjectId() {
		return this.projectId;
	}
	
	public void setProjectId(java.lang.Integer value) {
		this.projectId = value;
	}
	
	public java.lang.String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(java.lang.String value) {
		this.fileName = value;
	}
	
	public java.lang.String getFileType() {
		return this.fileType;
	}
	
	public void setFileType(java.lang.String value) {
		this.fileType = value;
	}
	
	public java.lang.String getFileBody() {
		return this.fileBody;
	}
	
	public void setFileBody(java.lang.String value) {
		this.fileBody = value;
	}
	
	public Integer getIsDir() {
		return this.isDir;
	}
	
	public void setIsDir(Integer value) {
		this.isDir = value;
	}
	
	public java.util.Date getOfflineTimeBegin() {
		return this.offlineTimeBegin;
	}
	
	public void setOfflineTimeBegin(java.util.Date value) {
		this.offlineTimeBegin = value;
	}	
	
	public java.util.Date getOfflineTimeEnd() {
		return this.offlineTimeEnd;
	}
	
	public void setOfflineTimeEnd(java.util.Date value) {
		this.offlineTimeEnd = value;
	}
	
	public java.lang.String getContacts() {
		return this.contacts;
	}
	
	public void setContacts(java.lang.String value) {
		this.contacts = value;
	}
	
	public java.lang.String getRemarks() {
		return this.remarks;
	}
	
	public void setRemarks(java.lang.String value) {
		this.remarks = value;
	}
	
	public Integer getIsValid() {
		return this.isValid;
	}
	
	public void setIsValid(Integer value) {
		this.isValid = value;
	}
	
	public java.util.Date getCreateTimeBegin() {
		return this.createTimeBegin;
	}
	
	public void setCreateTimeBegin(java.util.Date value) {
		this.createTimeBegin = value;
	}	
	
	public java.util.Date getCreateTimeEnd() {
		return this.createTimeEnd;
	}
	
	public void setCreateTimeEnd(java.util.Date value) {
		this.createTimeEnd = value;
	}
	
	public java.util.Date getUpdateTimeBegin() {
		return this.updateTimeBegin;
	}
	
	public void setUpdateTimeBegin(java.util.Date value) {
		this.updateTimeBegin = value;
	}	
	
	public java.util.Date getUpdateTimeEnd() {
		return this.updateTimeEnd;
	}
	
	public void setUpdateTimeEnd(java.util.Date value) {
		this.updateTimeEnd = value;
	}
	
	public java.lang.String getCreateUser() {
		return this.createUser;
	}
	
	public void setCreateUser(java.lang.String value) {
		this.createUser = value;
	}
	
	public java.lang.String getUpdateUser() {
		return this.updateUser;
	}
	
	public void setUpdateUser(java.lang.String value) {
		this.updateUser = value;
	}
	

	public java.lang.String getLineStatus() {
		return lineStatus;
	}

	public void setLineStatus(java.lang.String lineStatus) {
		this.lineStatus = lineStatus;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
}

