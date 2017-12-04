package com.duowan.meteor.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.duowan.meteor.dao.InstanceTaskDao;
import com.duowan.meteor.model.instance.InstanceTaskDB;
import com.duowan.meteor.model.query.InstanceTaskQuery;
import com.duowan.meteor.service.InstanceTaskService;

@Service("instanceTaskService")
@Transactional
public class InstanceTaskServiceImpl implements InstanceTaskService {

	@Autowired
	private InstanceTaskDao instanceTaskDao;

	@Override
	public int[] batchInsert(List<InstanceTaskDB> entityList) {
		return instanceTaskDao.batchInsert(entityList);
	}

	/**
	 * 清除历史数据
	 * 
	 * @param minKeepTime
	 * @return
	 */
	public int cleanHistory(Date minKeepTime) {
		return instanceTaskDao.cleanHistory(minKeepTime);
	}

	@Override
	public List<InstanceTaskDB> getByQuery(InstanceTaskQuery query) {
		return instanceTaskDao.getByQuery(query);
	}

	@Override
	public InstanceTaskDB getById(String instanceFlowId, Integer fileId) {
		Assert.notNull(instanceFlowId,"'instanceFlowId' must be not null");
	    Assert.notNull(fileId,"'fileId' must be not null");
	    return instanceTaskDao.getById(instanceFlowId, fileId);
	}
}
