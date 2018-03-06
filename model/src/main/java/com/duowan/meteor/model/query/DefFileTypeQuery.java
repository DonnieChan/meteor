/*
 * Copyright [duowan.com]
 * Web Site: http://www.duowan.com
 * Since 2005 - 2015
 */

package com.duowan.meteor.model.query;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * @author taosheng
 * @version 1.0
 * @since 1.0
 */
public class DefFileTypeQuery {

	private java.lang.String fileType;
	private java.lang.String fileTypeDesc;
	private java.lang.String fileTypeCategory;
	private java.lang.Integer dependFlag;

	public java.lang.String getFileType() {
		return this.fileType;
	}

	public void setFileType(java.lang.String value) {
		this.fileType = value;
	}

	public java.lang.String getFileTypeDesc() {
		return this.fileTypeDesc;
	}

	public void setFileTypeDesc(java.lang.String value) {
		this.fileTypeDesc = value;
	}

	public java.lang.String getFileTypeCategory() {
		return this.fileTypeCategory;
	}

	public void setFileTypeCategory(java.lang.String value) {
		this.fileTypeCategory = value;
	}
	
	
	public java.lang.Integer getDependFlag() {
		return dependFlag;
	}

	public void setDependFlag(java.lang.Integer dependFlag) {
		this.dependFlag = dependFlag;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}
