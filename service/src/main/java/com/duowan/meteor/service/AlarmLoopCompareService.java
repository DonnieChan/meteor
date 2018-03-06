package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmLoopCompare;

public interface AlarmLoopCompareService {

	/**
	 * 创建AlarmLoopCompare
	 **/
	public AlarmLoopCompare create(AlarmLoopCompare alarmLoopCompare);

	/**
	 * 更新AlarmLoopCompare
	 **/
	public AlarmLoopCompare update(AlarmLoopCompare alarmLoopCompare);

	/**
	 * 删除AlarmLoopCompare
	 **/
	public void removeById(int sourceTaskId);

	/**
	 * 根据ID得到AlarmLoopCompare
	 **/
	public AlarmLoopCompare getById(int sourceTaskId);

	public List<AlarmLoopCompare> getAllValid();

}
