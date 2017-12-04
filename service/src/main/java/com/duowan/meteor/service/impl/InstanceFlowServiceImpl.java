package com.duowan.meteor.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duowan.meteor.dao.InstanceFlowDao;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.query.InstanceFlowQuery;
import com.duowan.meteor.service.InstanceFlowService;

@Service("instanceFlowService")
@Transactional
public class InstanceFlowServiceImpl implements InstanceFlowService {

	@Autowired
	private InstanceFlowDao instanceFlowDao;

	@Override
	public int[] batchInsert(List<InstanceFlow> entityList) {
		return instanceFlowDao.batchInsert(entityList);
	}

	/**
	 * 清除历史数据
	 * 
	 * @param minKeepTime
	 * @return
	 */
	public int cleanHistory(Date minKeepTime) {
		return instanceFlowDao.cleanHistory(minKeepTime);
	}

	@Override
	public List<InstanceFlow> getByQuery(InstanceFlowQuery query) {
		return instanceFlowDao.getByQuery(query);
	}

}
