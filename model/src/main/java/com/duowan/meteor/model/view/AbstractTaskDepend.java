package com.duowan.meteor.model.view;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * 可依赖任务的共同属性
 * @author chenwu
 */
public abstract class AbstractTaskDepend extends AbstractBase {

	private static final long serialVersionUID = -6081451572261071241L;

	private Set<Integer> preDependSet = new HashSet<Integer>();
	private Set<Integer> postDependSet = new HashSet<Integer>();
	
	private Long beginSleepTime = 0L; // 单位为毫秒
	private Long finishSleepTime = 0L; // 单位为毫秒
	private Integer threadPoolSize = 1;
	private Integer maxRetryTimes = 2;
	private Long retryInterval = 0L; // 单位为毫秒
	private Integer isIgnoreError = 0;
	private String programClass;
	private Integer priority = 2;

	@Override
	public void doAssert() {
		super.doAssert();
		assertTrue(this.beginSleepTime != null, "beginSleepTime cannot be null!");
		assertTrue(this.finishSleepTime != null, "finishSleepTime cannot be null!");
		assertTrue(this.threadPoolSize != null, "threadPoolSize cannot be null!");
		assertTrue(this.maxRetryTimes != null, "maxRetryTimes cannot be null!");
		assertTrue(this.retryInterval != null, "retryInterval cannot be null!");
		assertTrue(this.isIgnoreError != null, "isIgnoreError cannot be null!");
		assertTrue(StringUtils.isNotBlank(this.programClass), "programClass cannot be blank!");
	}
    
    @Override
    public void doTrim() {
    	super.doTrim();
    	programClass = StringUtils.trim(programClass);
    }

	public Set<Integer> getPreDependSet() {
		return preDependSet;
	}

	public void setPreDependSet(Set<Integer> preDependSet) {
		this.preDependSet = preDependSet;
	}

	public Set<Integer> getPostDependSet() {
		return postDependSet;
	}

	public void setPostDependSet(Set<Integer> postDependSet) {
		this.postDependSet = postDependSet;
	}

	public Long getBeginSleepTime() {
		return beginSleepTime;
	}

	public void setBeginSleepTime(Long beginSleepTime) {
		this.beginSleepTime = beginSleepTime;
	}

	public Long getFinishSleepTime() {
		return finishSleepTime;
	}

	public void setFinishSleepTime(Long finishSleepTime) {
		this.finishSleepTime = finishSleepTime;
	}

	public Integer getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(Integer threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	public Integer getMaxRetryTimes() {
		return maxRetryTimes;
	}

	public void setMaxRetryTimes(Integer maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

	public Long getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(Long retryInterval) {
		this.retryInterval = retryInterval;
	}

	public Integer getIsIgnoreError() {
		return isIgnoreError;
	}

	public void setIsIgnoreError(Integer isIgnoreError) {
		this.isIgnoreError = isIgnoreError;
	}

	public String getProgramClass() {
		return programClass;
	}

	public void setProgramClass(String programClass) {
		this.programClass = programClass;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}
}
