package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmDataDelay;

public interface AlarmDataDelayDao {

	public void insert(AlarmDataDelay entity);

	public int update(AlarmDataDelay entity);

	public int deleteById(int sourceTaskId);

	public AlarmDataDelay getById(int sourceTaskId);

	public List<AlarmDataDelay> getAllValid();

}
