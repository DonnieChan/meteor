package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmSliceDelay;

public interface AlarmSliceDelayService {

	/**
	 * 创建AlarmSliceDelay
	 **/
	public AlarmSliceDelay create(AlarmSliceDelay alarmSliceDelay);

	/**
	 * 更新AlarmSliceDelay
	 **/
	public AlarmSliceDelay update(AlarmSliceDelay alarmSliceDelay);

	/**
	 * 删除AlarmSliceDelay
	 **/
	public void removeById(int sourceTaskId);

	/**
	 * 根据ID得到AlarmSliceDelay
	 **/
	public AlarmSliceDelay getById(int sourceTaskId);

	public List<AlarmSliceDelay> getAllValid();

}
