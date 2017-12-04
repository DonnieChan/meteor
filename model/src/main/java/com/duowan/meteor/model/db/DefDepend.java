package com.duowan.meteor.model.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * tableName: def_depend [DefDepend]
 * 
 * @author chenwu
 */
public class DefDepend implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	private java.lang.Integer fileId;

	private java.lang.Integer preFileId;

	private java.lang.Integer projectId;

	private java.lang.String remarks;

	private Integer isValid;

	private java.util.Date createTime;

	private java.util.Date updateTime;

	private java.lang.String createUser;

	private java.lang.String updateUser;

	public DefDepend() {
	}

	public DefDepend(Integer fileId) {
		this.fileId = fileId;
	}

	public DefDepend(java.lang.Integer fileId, java.lang.Integer preFileId) {
		this.fileId = fileId;
		this.preFileId = preFileId;
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
		return new HashCodeBuilder().append(getFileId()).append(getPreFileId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof DefDepend == false)
			return false;
		DefDepend other = (DefDepend) obj;
		return new EqualsBuilder().append(getFileId(), other.getFileId()).append(getPreFileId(), other.getPreFileId()).isEquals();
	}
}
