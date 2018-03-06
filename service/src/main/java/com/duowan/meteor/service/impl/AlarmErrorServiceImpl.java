package com.duowan.meteor.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.AlarmErrorDao;
import com.duowan.meteor.model.alarm.AlarmError;
import com.duowan.meteor.service.AlarmErrorService;

@Service("alarmErrorService")
public class AlarmErrorServiceImpl implements AlarmErrorService {

	protected static final Logger log = LoggerFactory.getLogger(AlarmErrorServiceImpl.class);

	@Autowired
	private AlarmErrorDao alarmErrorDao;

	/**
	 * 创建AlarmError
	 **/
	public AlarmError create(AlarmError alarmError) {
		Assert.notNull(alarmError, "'alarmError' must be not null");
		alarmErrorDao.insert(alarmError);
		return alarmError;
	}

	/**
	 * 更新AlarmError
	 **/
	public AlarmError update(AlarmError alarmError) {
		Assert.notNull(alarmError, "'alarmError' must be not null");
		alarmErrorDao.update(alarmError);
		return alarmError;
	}

	/**
	 * 删除AlarmError
	 **/
	public void removeById(int sourceTaskId) {
		alarmErrorDao.deleteById(sourceTaskId);
	}

	/**
	 * 根据ID得到AlarmError
	 **/
	public AlarmError getById(int sourceTaskId) {
		return alarmErrorDao.getById(sourceTaskId);
	}

	@Override
	public List<AlarmError> getAllValid() {
		return alarmErrorDao.getAllValid();
	}

}
