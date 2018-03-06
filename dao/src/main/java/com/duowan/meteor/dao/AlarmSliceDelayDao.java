package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmSliceDelay;


public interface AlarmSliceDelayDao {
	
	public void insert(AlarmSliceDelay entity);
	
	public int update(AlarmSliceDelay entity);

	public int deleteById(int sourceTaskId);
	
	public AlarmSliceDelay getById(int sourceTaskId);

	public List<AlarmSliceDelay> getAllValid();
	
}
