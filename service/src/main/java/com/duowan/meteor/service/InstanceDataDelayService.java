package com.duowan.meteor.service;

import java.util.Date;
import java.util.List;

import com.duowan.meteor.model.instance.InstanceDataDelay;


public interface InstanceDataDelayService {

	/** 
	 * 创建InstanceDataDelay
	 **/
	public InstanceDataDelay create(InstanceDataDelay instanceDataDelay);
	
	/** 
	 * 更新InstanceDataDelay
	 **/	
    public InstanceDataDelay update(InstanceDataDelay instanceDataDelay);
    
	/** 
	 * 删除InstanceDataDelay
	 **/
    public void removeById(Date ttime, int sourceTaskId, int taskId);
    
	/** 
	 * 根据ID得到InstanceDataDelay
	 **/    
    public InstanceDataDelay getById(Date ttime, int sourceTaskId, int taskId);

	public void batchInsert(List<InstanceDataDelay> list);

	public void cleanHistory(Date minKeepTime);

	public List<InstanceDataDelay> getRecentDelays(Date startCreateTime);

}
