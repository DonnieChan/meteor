package com.duowan.meteor.service;

import java.util.List;

import com.duowan.meteor.model.alarm.AlarmError;

public interface AlarmErrorService {

	/**
	 * 创建AlarmError
	 **/
	public AlarmError create(AlarmError alarmError);

	/**
	 * 更新AlarmError
	 **/
	public AlarmError update(AlarmError alarmError);

	/**
	 * 删除AlarmError
	 **/
	public void removeById(int sourceTaskId);

	/**
	 * 根据ID得到AlarmError
	 **/
	public AlarmError getById(int sourceTaskId);

	public List<AlarmError> getAllValid();

}
