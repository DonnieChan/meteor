package com.duowan.meteor.model.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * tableName: def_file_sys [DefFileSys]
 * 
 * @author chenwu
 */
public class DefFileSys implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	private java.lang.Integer fileId;

	private java.lang.Integer parentFileId;

	private java.lang.Integer projectId;

	private java.lang.String fileName;

	private java.lang.String fileType;

	private java.lang.String fileBody;

	private Integer isDir;

	private java.util.Date offlineTime;

	private java.lang.String contacts;

	private java.lang.String remarks;

	private Integer isValid;

	private java.util.Date createTime;

	private java.util.Date updateTime;

	private java.lang.String createUser;

	private java.lang.String updateUser;

	public DefFileSys() {
	}

	public DefFileSys(java.lang.Integer fileId) {
		this.fileId = fileId;
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

	public java.util.Date getOfflineTime() {
		return this.offlineTime;
	}

	public void setOfflineTime(java.util.Date value) {
		this.offlineTime = value;
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

	public java.util.Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(java.util.Date value) {
		this.createTime = value;
	}

	public java.util.Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(java.util.Date value) {
		this.updateTime = value;
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

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getFileId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof DefFileSys == false)
			return false;
		DefFileSys other = (DefFileSys) obj;
		return new EqualsBuilder().append(getFileId(), other.getFileId()).isEquals();
	}
}
