package com.duowan.meteor.model.query;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author taosheng
 */
public class DefFileSysExtQuery extends DefFileSysQuery {

	/** begin: from他表的描述信息 */
	private java.lang.String projectName;
	private java.lang.String fileTypeDesc;
	private java.lang.String fileTypeCategory;
	private java.lang.Integer isCanDepend;

	/** end: from他表的描述信息 */

	public java.lang.String getProjectName() {
		return projectName;
	}

	public void setProjectName(java.lang.String projectName) {
		this.projectName = projectName;
	}

	public java.lang.String getFileTypeDesc() {
		return fileTypeDesc;
	}

	public void setFileTypeDesc(java.lang.String fileTypeDesc) {
		this.fileTypeDesc = fileTypeDesc;
	}

	public java.lang.String getFileTypeCategory() {
		return fileTypeCategory;
	}

	public void setFileTypeCategory(java.lang.String fileTypeCategory) {
		this.fileTypeCategory = fileTypeCategory;
	}

	public java.lang.Integer getIsCanDepend() {
		return isCanDepend;
	}

	public void setIsCanDepend(java.lang.Integer isCanDepend) {
		this.isCanDepend = isCanDepend;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
