package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmDataDelay;


public interface AlarmDataDelayService {

	/**
	 * 创建AlarmDataDelay
	 **/
	public AlarmDataDelay create(AlarmDataDelay alarmDataDelay);

	/**
	 * 更新AlarmDataDelay
	 **/
	public AlarmDataDelay update(AlarmDataDelay alarmDataDelay);

	/**
	 * 删除AlarmDataDelay
	 **/
	public void removeById(int sourceTaskId);

	/**
	 * 根据ID得到AlarmDataDelay
	 **/
	public AlarmDataDelay getById(int sourceTaskId);

	public List<AlarmDataDelay> getAllValid();

}
