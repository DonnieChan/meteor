package com.duowan.meteor.model.view;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.duowan.meteor.model.enumtype.FileType;

/**
 * 文件基础属性
 * 
 * @author chenwu
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "CLASS")
public abstract class AbstractBase implements java.io.Serializable {

	private static final long serialVersionUID = 5423601933235285930L;

	private java.lang.Integer fileId;

	private java.lang.Integer parentFileId = 0;

	private java.lang.Integer projectId;

	private java.lang.String fileName;

	private java.lang.String fileType;

	private Integer isDir = 0;

	private java.lang.String remarks;

	private Integer isValid = 1;

	private java.util.Date createTime = new Date();

	private java.util.Date updateTime = new Date();

	private java.util.Date offlineTime = DateUtils.addYears(new Date(), 1);

	private java.lang.String contacts;

	private java.lang.String createUser;

	private java.lang.String updateUser;

	public void doAssert() {
		// assertTrue(this.fileId != null, "fileId cannot be null!");
		assertTrue(this.parentFileId != null, "parentFileId cannot be null!");
		assertTrue(this.projectId != null, "projectId cannot be null!");
		assertTrue(StringUtils.isNotBlank(this.fileName), "fileName cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.fileType), "fileType cannot be blank!");
		Enum.valueOf(FileType.class, this.fileType);
		assertTrue(this.isDir != null, "isDir cannot be null!");
		assertTrue(this.isValid != null, "isValid cannot be null!");
		assertTrue(this.createTime != null, "createTime cannot be null!");
		assertTrue(this.updateTime != null, "updateTime cannot be null!");
		assertTrue(this.offlineTime != null, "offlineTime cannot be null!");
		assertTrue(StringUtils.isNotBlank(this.createUser), "createUser cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.updateUser), "updateUser cannot be blank!");
	}

	public void assertTrue(boolean expression, String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

	public void doTrim() {
		fileName = StringUtils.trim(fileName);
		fileType = StringUtils.trim(fileType);
		remarks = StringUtils.trim(remarks);
		contacts = StringUtils.trim(contacts);
		createUser = StringUtils.trim(createUser);
		updateUser = StringUtils.trim(updateUser);
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

	public Integer getIsDir() {
		return this.isDir;
	}

	public void setIsDir(Integer value) {
		this.isDir = value;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getFileId()).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AbstractBase == false)
			return false;
		AbstractBase other = (AbstractBase) obj;
		return new EqualsBuilder().append(getFileId(), other.getFileId()).isEquals();
	}
}
