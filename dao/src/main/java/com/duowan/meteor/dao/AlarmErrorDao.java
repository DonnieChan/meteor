package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmError;

public interface AlarmErrorDao {

	public void insert(AlarmError entity);

	public int update(AlarmError entity);

	public int deleteById(int sourceTaskId);

	public AlarmError getById(int sourceTaskId);

	public List<AlarmError> getAllValid();

}
