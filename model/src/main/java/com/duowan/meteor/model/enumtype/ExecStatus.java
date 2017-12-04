package com.duowan.meteor.model.enumtype;

public enum ExecStatus {

	Init("Init", "初始化"), Running("Running", "运行中"), Success("Success", "成功"), Fail("Fail", "失败");
	
	private String execCode;
	private String execName;
	
	private ExecStatus(String execCode, String execName) {
		this.execCode = execCode;
		this.execName = execName;
	}

	public String getExecCode() {
		return execCode;
	}

	public void setExecCode(String execCode) {
		this.execCode = execCode;
	}

	public String getExecName() {
		return execName;
	}

	public void setExecName(String execName) {
		this.execName = execName;
	}

	public static ExecStatus getByName(String name) {
		return Enum.valueOf(ExecStatus.class, name);
	}
}
