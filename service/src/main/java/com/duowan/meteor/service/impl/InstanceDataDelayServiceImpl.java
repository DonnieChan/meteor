package com.duowan.meteor.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.InstanceDataDelayDao;
import com.duowan.meteor.model.instance.InstanceDataDelay;
import com.duowan.meteor.service.InstanceDataDelayService;

@Service("instanceDataDelayService")
public class InstanceDataDelayServiceImpl implements InstanceDataDelayService {

	protected static final Logger log = LoggerFactory.getLogger(InstanceDataDelayServiceImpl.class);

	@Autowired
	private InstanceDataDelayDao instanceDataDelayDao;

	/**
	 * 创建InstanceDataDelay
	 **/
	public InstanceDataDelay create(InstanceDataDelay instanceDataDelay) {
		Assert.notNull(instanceDataDelay, "'instanceDataDelay' must be not null");
		instanceDataDelayDao.insert(instanceDataDelay);
		return instanceDataDelay;
	}

	/**
	 * 更新InstanceDataDelay
	 **/
	public InstanceDataDelay update(InstanceDataDelay instanceDataDelay) {
		Assert.notNull(instanceDataDelay, "'instanceDataDelay' must be not null");
		instanceDataDelayDao.update(instanceDataDelay);
		return instanceDataDelay;
	}

	/**
	 * 删除InstanceDataDelay
	 **/
	public void removeById(Date ttime, int sourceTaskId, int taskId) {
		instanceDataDelayDao.deleteById(ttime, sourceTaskId, taskId);
	}

	/**
	 * 根据ID得到InstanceDataDelay
	 **/
	public InstanceDataDelay getById(Date ttime, int sourceTaskId, int taskId) {
		return instanceDataDelayDao.getById(ttime, sourceTaskId, taskId);
	}

	@Override
	public void batchInsert(List<InstanceDataDelay> list) {
		instanceDataDelayDao.batchInsert(list);
	}

	@Override
	public void cleanHistory(Date minKeepTime) {
		instanceDataDelayDao.cleanHistory(minKeepTime);
	}

	@Override
	public List<InstanceDataDelay> getRecentDelays(Date startCreateTime) {
		return instanceDataDelayDao.getRecentDelays(startCreateTime);
	}
}
