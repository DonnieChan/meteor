package com.duowan.meteor.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.AlarmLoopCompareDao;
import com.duowan.meteor.model.alarm.AlarmLoopCompare;
import com.duowan.meteor.service.AlarmLoopCompareService;

@Service("alarmLoopCompareService")
public class AlarmLoopCompareServiceImpl implements AlarmLoopCompareService {

	protected static final Logger log = LoggerFactory.getLogger(AlarmLoopCompareServiceImpl.class);

	@Autowired
	private AlarmLoopCompareDao alarmLoopCompareDao;

	/**
	 * 创建AlarmLoopCompare
	 **/
	public AlarmLoopCompare create(AlarmLoopCompare alarmLoopCompare) {
		Assert.notNull(alarmLoopCompare, "'alarmLoopCompare' must be not null");
		alarmLoopCompareDao.insert(alarmLoopCompare);
		return alarmLoopCompare;
	}

	/**
	 * 更新AlarmLoopCompare
	 **/
	public AlarmLoopCompare update(AlarmLoopCompare alarmLoopCompare) {
		Assert.notNull(alarmLoopCompare, "'alarmLoopCompare' must be not null");
		alarmLoopCompareDao.update(alarmLoopCompare);
		return alarmLoopCompare;
	}

	/**
	 * 删除AlarmLoopCompare
	 **/
	public void removeById(int sourceTaskId) {
		alarmLoopCompareDao.deleteById(sourceTaskId);
	}

	/**
	 * 根据ID得到AlarmLoopCompare
	 **/
	public AlarmLoopCompare getById(int sourceTaskId) {
		return alarmLoopCompareDao.getById(sourceTaskId);
	}

	@Override
	public List<AlarmLoopCompare> getAllValid() {
		return alarmLoopCompareDao.getAllValid();
	}
}
