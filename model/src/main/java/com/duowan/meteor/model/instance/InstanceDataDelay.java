package com.duowan.meteor.model.instance;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class InstanceDataDelay implements java.io.Serializable {

	private static final long serialVersionUID = 5454155825314635342L;

	/**
	 * 时间 db_column: ttime
	 */
	private java.util.Date ttime;

	/**
	 * 源头任务ID db_column: source_task_id
	 */

	private java.lang.Integer sourceTaskId;

	/**
	 * 任务id db_column: task_id
	 */

	private java.lang.Integer taskId;

	/**
	 * 延迟毫秒数 db_column: delay_millis
	 */
	private java.lang.Integer delayMillis;
	
	private java.util.Date createTime = new Date();

	public InstanceDataDelay() {
	}

	public InstanceDataDelay(java.util.Date ttime, java.lang.Integer sourceTaskId, java.lang.Integer taskId) {
		this.ttime = ttime;
		this.sourceTaskId = sourceTaskId;
		this.taskId = taskId;
	}

	public java.util.Date getTtime() {
		return this.ttime;
	}

	public void setTtime(java.util.Date value) {
		this.ttime = value;
	}

	public java.lang.Integer getSourceTaskId() {
		return this.sourceTaskId;
	}

	public void setSourceTaskId(java.lang.Integer value) {
		this.sourceTaskId = value;
	}

	public java.lang.Integer getTaskId() {
		return this.taskId;
	}

	public void setTaskId(java.lang.Integer value) {
		this.taskId = value;
	}

	public java.lang.Integer getDelayMillis() {
		return this.delayMillis;
	}

	public void setDelayMillis(java.lang.Integer value) {
		this.delayMillis = value;
	}

	public java.util.Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(java.util.Date createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getTtime()).append(getSourceTaskId()).append(getTaskId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj instanceof InstanceDataDelay == false)
			return false;
		InstanceDataDelay other = (InstanceDataDelay) obj;
		return new EqualsBuilder().append(getTtime(), other.getTtime()).append(getSourceTaskId(), other.getSourceTaskId()).append(getTaskId(), other.getTaskId()).isEquals();
	}
}
