package com.duowan.meteor.service;

import java.util.Date;
import java.util.List;

import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.query.InstanceFlowQuery;

public interface InstanceFlowService {

	public int[] batchInsert(List<InstanceFlow> entityList);

	public int cleanHistory(Date minKeepTime);

	public List<InstanceFlow> getByQuery(InstanceFlowQuery query);
}
