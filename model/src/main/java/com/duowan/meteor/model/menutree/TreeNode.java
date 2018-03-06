package com.duowan.meteor.model.menutree;

import java.util.Map;

public class TreeNode {
	
	private String name;
	private String caption;
	private String uniqueName;
	private String parentUniqueName;
	private Map<String,String> ext;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String getUniqueName() {
		return uniqueName;
	}
	public void setUniqueName(String uniqueName) {
		this.uniqueName = uniqueName;
	}
	public String getParentUniqueName() {
		return parentUniqueName;
	}
	public void setParentUniqueName(String parentUniqueName) {
		this.parentUniqueName = parentUniqueName;
	}
	public Map<String, String> getExt() {
		return ext;
	}
	public void setExt(Map<String, String> ext) {
		this.ext = ext;
	}

	
}
