package com.duowan.meteor.model.alarm;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class AlarmLoopCompare implements java.io.Serializable {

	private static final long serialVersionUID = 5454155825314635342L;

	/**
	 * 源头任务ID db_column: source_task_id
	 */

	private java.lang.Integer sourceTaskId;

	/**
	 * 环比下降比率 db_column: reduce_rate
	 */
	private java.lang.Double reduceRate;

	/**
	 * 报警最小间隔 db_column: alarm_gap_min
	 */
	private java.lang.Integer alarmGapMin;

	/**
	 * 报警用户列表，英文逗号分隔 db_column: alarm_users
	 */
	private java.lang.String alarmUsers;

	/**
	 * 备注 db_column: remarks
	 */
	private java.lang.String remarks;

	/**
	 * 是否还可用，1可用，0已下线不可用 db_column: is_valid
	 */
	private Integer isValid;

	/**
	 * 创建时间 db_column: create_time
	 */
	private java.util.Date createTime;

	/**
	 * 更新时间 db_column: update_time
	 */

	private java.util.Date updateTime;

	/**
	 * 创建用户 db_column: create_user
	 */
	private java.lang.String createUser;

	/**
	 * 更新用户 db_column: update_user
	 */
	private java.lang.String updateUser;

	public AlarmLoopCompare() {
	}

	public AlarmLoopCompare(java.lang.Integer sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}

	public java.lang.Integer getSourceTaskId() {
		return this.sourceTaskId;
	}

	public void setSourceTaskId(java.lang.Integer value) {
		this.sourceTaskId = value;
	}

	public java.lang.Double getReduceRate() {
		return this.reduceRate;
	}

	public void setReduceRate(java.lang.Double value) {
		this.reduceRate = value;
	}

	public java.lang.Integer getAlarmGapMin() {
		return this.alarmGapMin;
	}

	public void setAlarmGapMin(java.lang.Integer value) {
		this.alarmGapMin = value;
	}

	public java.lang.String getAlarmUsers() {
		return this.alarmUsers;
	}

	public void setAlarmUsers(java.lang.String value) {
		this.alarmUsers = value;
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
		return ToStringBuilder.reflectionToString(this);
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getSourceTaskId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof AlarmLoopCompare == false)
			return false;
		AlarmLoopCompare other = (AlarmLoopCompare) obj;
		return new EqualsBuilder().append(getSourceTaskId(), other.getSourceTaskId()).isEquals();
	}
}
