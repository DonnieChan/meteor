package com.duowan.meteor.model.alarm;

public class LoopRate implements java.io.Serializable {

	private static final long serialVersionUID = -6552697493057655916L;

	private java.lang.Integer sourceTaskId;

	private java.lang.Double loopRate;

	public LoopRate() {
	}

	public LoopRate(java.lang.Integer sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}

	public java.lang.Integer getSourceTaskId() {
		return this.sourceTaskId;
	}

	public void setSourceTaskId(java.lang.Integer value) {
		this.sourceTaskId = value;
	}

	public java.lang.Double getLoopRate() {
		return loopRate;
	}

	public void setLoopRate(java.lang.Double loopRate) {
		this.loopRate = loopRate;
	}

}
