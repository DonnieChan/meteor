package com.duowan.meteor.dao;

import java.util.Date;
import java.util.List;

import com.duowan.meteor.model.instance.InstanceDataDelay;


public interface InstanceDataDelayDao {
	
	public void insert(InstanceDataDelay entity);
	
	public int update(InstanceDataDelay entity);

	public int deleteById(Date ttime, int sourceTaskId, int taskId);
	
	public InstanceDataDelay getById(Date ttime, int sourceTaskId, int taskId);

	public void batchInsert(List<InstanceDataDelay> list);

	public int cleanHistory(Date minKeepTime);

	public List<InstanceDataDelay> getRecentDelays(Date startCreateTime);
	
}
