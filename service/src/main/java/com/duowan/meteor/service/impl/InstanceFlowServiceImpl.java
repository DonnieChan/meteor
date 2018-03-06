package com.duowan.meteor.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duowan.meteor.dao.InstanceFlowDao;
import com.duowan.meteor.model.alarm.LoopRate;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.query.InstanceFlowQuery;
import com.duowan.meteor.service.InstanceFlowService;

@Service("instanceFlowService")
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

	@Override
	public List<InstanceFlow> getRecentInstances(Date startCreateTime, String status) {
		return instanceFlowDao.getRecentInstances(startCreateTime, status);
	}

	@Override
	public List<LoopRate> getLoopRates(Date date0, Date date1, Date date2) {
		return instanceFlowDao.getLoopRates(date0, date1, date2);
	}

}
