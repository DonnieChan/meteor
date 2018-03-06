package com.duowan.meteor.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.AlarmDataDelayDao;
import com.duowan.meteor.model.alarm.AlarmDataDelay;
import com.duowan.meteor.service.AlarmDataDelayService;

@Service("alarmDataDelayService")
public class AlarmDataDelayServiceImpl implements AlarmDataDelayService {

	protected static final Logger log = LoggerFactory.getLogger(AlarmDataDelayServiceImpl.class);

	@Autowired
	private AlarmDataDelayDao alarmDataDelayDao;

	/**
	 * 创建AlarmDataDelay
	 **/
	public AlarmDataDelay create(AlarmDataDelay alarmDataDelay) {
		Assert.notNull(alarmDataDelay, "'alarmDataDelay' must be not null");
		alarmDataDelayDao.insert(alarmDataDelay);
		return alarmDataDelay;
	}

	/**
	 * 更新AlarmDataDelay
	 **/
	public AlarmDataDelay update(AlarmDataDelay alarmDataDelay) {
		Assert.notNull(alarmDataDelay, "'alarmDataDelay' must be not null");
		alarmDataDelayDao.update(alarmDataDelay);
		return alarmDataDelay;
	}

	/**
	 * 删除AlarmDataDelay
	 **/
	public void removeById(int sourceTaskId) {
		alarmDataDelayDao.deleteById(sourceTaskId);
	}

	/**
	 * 根据ID得到AlarmDataDelay
	 **/
	public AlarmDataDelay getById(int sourceTaskId) {
		return alarmDataDelayDao.getById(sourceTaskId);
	}

	@Override
	public List<AlarmDataDelay> getAllValid() {
		return alarmDataDelayDao.getAllValid();
	}

}
