package com.duowan.meteor.dao;

import java.util.Date;
import java.util.List;

import com.duowan.meteor.model.instance.InstanceTaskDB;
import com.duowan.meteor.model.query.InstanceTaskQuery;

public interface InstanceTaskDao {
	
	public int[] batchInsert(List<InstanceTaskDB> entityList);
	
	public int cleanHistory(Date minKeepTime);

	public List<InstanceTaskDB> getByQuery(InstanceTaskQuery query);

	public InstanceTaskDB getById(String instanceFlowId, int fileId);

}
