package com.duowan.meteor.model.query;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * @author chenwu
 */
public class DefDependQuery {

	private java.lang.Integer fileId;

	private java.lang.Integer preFileId;

	private java.lang.Integer projectId;

	private java.lang.String remarks;

	private Integer isValid;

	private java.util.Date createTimeBegin;
	private java.util.Date createTimeEnd;

	private java.util.Date updateTimeBegin;
	private java.util.Date updateTimeEnd;

	private java.lang.String createUser;

	private java.lang.String updateUser;
	
	private java.util.Date offlineTimeBegin;
	private java.util.Date offlineTimeEnd;

	public DefDependQuery() {
	}
	
	public DefDependQuery(Integer fileId) {
		this.fileId = fileId;
	}

	public java.lang.Integer getFileId() {
		return this.fileId;
	}
	
	public void setFileId(java.lang.Integer value) {
		this.fileId = value;
	}
	
	public java.lang.Integer getPreFileId() {
		return this.preFileId;
	}
	
	public void setPreFileId(java.lang.Integer value) {
		this.preFileId = value;
	}
	
	public java.lang.Integer getProjectId() {
		return this.projectId;
	}
	
	public void setProjectId(java.lang.Integer value) {
		this.projectId = value;
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

	public java.util.Date getOfflineTimeBegin() {
		return offlineTimeBegin;
	}

	public void setOfflineTimeBegin(java.util.Date offlineTimeBegin) {
		this.offlineTimeBegin = offlineTimeBegin;
	}

	public java.util.Date getOfflineTimeEnd() {
		return offlineTimeEnd;
	}

	public void setOfflineTimeEnd(java.util.Date offlineTimeEnd) {
		this.offlineTimeEnd = offlineTimeEnd;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
	
}

