package com.duowan.meteor.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.duowan.meteor.model.enumtype.ExecStatus;
import com.duowan.meteor.model.instance.InstanceTaskDB;
import com.duowan.meteor.service.InstanceTaskService;
import com.duowan.meteor.service.base.BaseServiceTestCase;

public class InstanceTaskServiceImplTest extends BaseServiceTestCase {

	private InstanceTaskService instanceTaskService;

	@Before
	public void setUp() throws ParseException {
		instanceTaskService = (InstanceTaskService) getBean("instanceTaskService");
	}
	
	@Test
	public void testBatchInsert(){
		List<InstanceTaskDB> instanceTaskDBList = new ArrayList<InstanceTaskDB>();
		for (int i=0; i<10; i++) {
			InstanceTaskDB instanceDB = new InstanceTaskDB();
			instanceDB.setInstanceFlowId(UUID.randomUUID().toString().replace("-", ""));
			instanceDB.setFileId(100);
			instanceDB.setFileBody("{xxxx}");
			instanceDB.setReadyTime(new Date());
			instanceDB.setStartTime(new Date());
			instanceDB.setEndTime(new Date());
			instanceDB.setStatus(ExecStatus.Success.name());
			instanceDB.setRetriedTimes(2);
			instanceDB.setLog(null);
			instanceDB.setPoolActiveCount(10);
			instanceDB.setPoolQueueSize(10);
			instanceTaskDBList.add(instanceDB);
		}
		instanceTaskService.batchInsert(instanceTaskDBList);
	}

	@Test
	public void testCleanHistory(){
		instanceTaskService.cleanHistory(new Date());
	}
}
