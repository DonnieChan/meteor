package com.duowan.meteor.model.db;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 页面 中 所需要的附近描述信息
 * 
 * @author taosheng
 *
 */
public class DefFileSysExt extends DefFileSys {
	private static final long serialVersionUID = 5454155825314635342L;

	/** begin: from他表的描述信息 */
	private java.lang.String projectName;
	private java.lang.String fileTypeDesc;
	private java.lang.String fileTypeCategory;
	private java.lang.Integer isCanDepend;

	@SuppressWarnings("unused")
	private java.lang.String descInfoOnView; // 页面展示的描述信息

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
		// 注意！！
		if (getIsDir() == 1) {
			return 0;
		}
		return isCanDepend;
	}

	public void setIsCanDepend(java.lang.Integer isCanDepend) {
		this.isCanDepend = isCanDepend;
	}

	public java.lang.String getDescInfoOnView() {
		// 注意！！
		return toStringOnView();
	}

	public void setDescInfoOnView(java.lang.String descInfoOnView) {
		this.descInfoOnView = descInfoOnView;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getFileId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DefFileSys == false) {
			return false;
		}
		DefFileSys other = (DefFileSys) obj;
		return new EqualsBuilder().append(getFileId(), other.getFileId()).isEquals();
	}

	/**
	 * 页面上展示该file的信息
	 * @return
	 */
	public String toStringOnView() {
		String type = getIsDir() == 1 ? "目录" : "任务";
		String result = type + "信息：" + "\n";
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		result += type + "代号 = " + getFileId() + "\n";
		result += "所属项目 = " + StringUtils.trimToEmpty(getProjectName()) + "\n";
		result += "是否目录 = " + (getIsDir() == 1 ? "是" : "否") + "\n";
		result += "是否有效 = " + (getIsValid() == 1 ? "是" : "否") + "\n";
		result += type + "名称 = " + StringUtils.trimToEmpty(getFileName()) + "\n";
		if (getIsDir() == 0) {
			result += type + "类型 = " + StringUtils.trimToEmpty(getFileTypeDesc()) + "\n";
			result += "成为前缀依赖 = " + (getIsCanDepend() == 1 ? "可以" : "不可以") + "\n";
		}
		result += "更新用户 = " + StringUtils.trimToEmpty(getUpdateUser()) + "\n";
		result += "创建用户 = " + StringUtils.trimToEmpty(getCreateUser()) + "\n";
		result += "更新时间 = " + (getUpdateTime() == null ? "" : fmt.format(getUpdateTime())) + "\n";
		result += "创建时间 = " + (getCreateTime() == null ? "" : fmt.format(getCreateTime())) + "\n";
		result += "下线时间 = " + (getOfflineTime() == null ? "" : fmt.format(getOfflineTime())) + "\n";
		result += "备注信息 = " + StringUtils.trimToEmpty(getRemarks()) + "\n";

		// result = "[ " + StringUtils.trim(result) + " ]";
		return StringUtils.trim(result);
	}

	public String toStringOnTreeToolTips() {
		String toolTips = "Id=" + getFileId()
				+ "/" + "名称=" + StringUtils.trimToEmpty(getFileName())
				+ "/" + "类型=" + StringUtils.trimToEmpty(getFileTypeDesc())
				+ "/" + "有效=" + (getIsValid() == 1 ? "是" : "否")
				+ "/" + (getIsCanDepend() == 1 ? "可以" : "不可以") + "成为前缀依赖"
				+ "/" + "修改人=" + StringUtils.trimToEmpty(getUpdateUser())
				+ "/" + "创建人=" + StringUtils.trimToEmpty(getCreateUser())
				+ "/" + "备注=" + StringUtils.trimToEmpty(getRemarks());

		return toolTips;
	}

}