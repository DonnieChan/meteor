package com.duowan.meteor.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.AlarmSliceDelayDao;
import com.duowan.meteor.model.alarm.AlarmSliceDelay;
import com.duowan.meteor.service.AlarmSliceDelayService;

@Service("alarmSliceDelayService")
public class AlarmSliceDelayServiceImpl implements AlarmSliceDelayService {

	protected static final Logger log = LoggerFactory.getLogger(AlarmSliceDelayServiceImpl.class);

	@Autowired
	private AlarmSliceDelayDao alarmSliceDelayDao;

	/**
	 * 创建AlarmSliceDelay
	 **/
	public AlarmSliceDelay create(AlarmSliceDelay alarmSliceDelay) {
		Assert.notNull(alarmSliceDelay, "'alarmSliceDelay' must be not null");
		alarmSliceDelayDao.insert(alarmSliceDelay);
		return alarmSliceDelay;
	}

	/**
	 * 更新AlarmSliceDelay
	 **/
	public AlarmSliceDelay update(AlarmSliceDelay alarmSliceDelay) {
		Assert.notNull(alarmSliceDelay, "'alarmSliceDelay' must be not null");
		alarmSliceDelayDao.update(alarmSliceDelay);
		return alarmSliceDelay;
	}

	/**
	 * 删除AlarmSliceDelay
	 **/
	public void removeById(int sourceTaskId) {
		alarmSliceDelayDao.deleteById(sourceTaskId);
	}

	/**
	 * 根据ID得到AlarmSliceDelay
	 **/
	public AlarmSliceDelay getById(int sourceTaskId) {
		return alarmSliceDelayDao.getById(sourceTaskId);
	}

	@Override
	public List<AlarmSliceDelay> getAllValid() {
		return alarmSliceDelayDao.getAllValid();
	}
}
