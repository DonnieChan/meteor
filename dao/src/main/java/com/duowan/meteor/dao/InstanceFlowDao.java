package com.duowan.meteor.dao;

import java.util.Date;
import java.util.List;

import com.duowan.meteor.model.alarm.LoopRate;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.query.InstanceFlowQuery;

public interface InstanceFlowDao {

	public int[] batchInsert(List<InstanceFlow> entityList);

	public int cleanHistory(Date minKeepTime);

	public List<InstanceFlow> getByQuery(InstanceFlowQuery query);

	public List<InstanceFlow> getRecentInstances(Date startCreateTime, String status);

	public List<LoopRate> getLoopRates(Date date0, Date date1, Date date2);
}
