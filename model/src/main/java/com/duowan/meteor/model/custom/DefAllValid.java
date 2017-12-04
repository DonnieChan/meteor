package com.duowan.meteor.model.custom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.duowan.meteor.model.view.AbstractBase;

/**
 * 所有流程及其任务定义
 * @author chenwu
 */
public class DefAllValid {

	private Map<Integer, AbstractBase> defAllMap = new HashMap<Integer, AbstractBase>();
	private Set<Integer> importQueueSet = new HashSet<Integer>();
	private Set<Integer> cronSet = new HashSet<Integer>();
	
	/**
	 * @return the defAllMap
	 */
	public Map<Integer, AbstractBase> getDefAllMap() {
		return defAllMap;
	}

	/**
	 * @param defAllMap the defAllMap to set
	 */
	public void setDefAllMap(Map<Integer, AbstractBase> defAllMap) {
		this.defAllMap = defAllMap;
	}

	public Set<Integer> getImportQueueSet() {
		return importQueueSet;
	}

	public void setImportQueueSet(Set<Integer> importQueueSet) {
		this.importQueueSet = importQueueSet;
	}

	public Set<Integer> getCronSet() {
		return cronSet;
	}

	public void setCronSet(Set<Integer> cronSet) {
		this.cronSet = cronSet;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
