package com.duowan.meteor.dao;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmLoopCompare;

public interface AlarmLoopCompareDao {

	public void insert(AlarmLoopCompare entity);

	public int update(AlarmLoopCompare entity);

	public int deleteById(int sourceTaskId);

	public AlarmLoopCompare getById(int sourceTaskId);

	public List<AlarmLoopCompare> getAllValid();

}
