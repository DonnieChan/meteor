/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.model.db;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * tableName: def_project [DefProject]
 * 
 * @author taosheng
 * @version 1.0
 * @since 1.0
 */
public class DefProject implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	private java.lang.Integer projectId;
	private java.lang.String projectName;
	private java.lang.String remarks;
	private Integer isValid;
	private java.util.Date createTime;
	private java.util.Date updateTime;
	private java.lang.String createUser;
	private java.lang.String updateUser;

	public DefProject() {
	}

	public DefProject(java.lang.Integer projectId) {
		this.projectId = projectId;
	}

	public java.lang.Integer getProjectId() {
		return this.projectId;
	}

	public void setProjectId(java.lang.Integer value) {
		this.projectId = value;
	}

	public java.lang.String getProjectName() {
		return this.projectName;
	}

	public void setProjectName(java.lang.String value) {
		this.projectName = value;
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
		return new HashCodeBuilder().append(getProjectId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof DefProject == false)
			return false;
		DefProject other = (DefProject) obj;
		return new EqualsBuilder().append(getProjectId(), other.getProjectId()).isEquals();
	}
}
