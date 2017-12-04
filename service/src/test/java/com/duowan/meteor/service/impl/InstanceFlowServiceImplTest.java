package com.duowan.meteor.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.duowan.meteor.model.enumtype.ExecStatus;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.service.InstanceFlowService;
import com.duowan.meteor.service.base.BaseServiceTestCase;

public class InstanceFlowServiceImplTest extends BaseServiceTestCase {

	private InstanceFlowService instanceFlowService;

	@Before
	public void setUp() throws ParseException {
		instanceFlowService = (InstanceFlowService) getBean("instanceFlowService");
	}
	
	@Test
	public void testBatchInsert(){
		List<InstanceFlow> instanceList = new ArrayList<InstanceFlow>();
		for (int i=0; i<10; i++) {
			InstanceFlow instance = new InstanceFlow();
			instance.setInstanceFlowId(UUID.randomUUID().toString().replace("-", ""));
			instance.setSourceTaskId(100);
			instance.setInitTime(new Date());
			instance.setStartTime(new Date());
			instance.setEndTime(new Date());
			instance.setStatus(ExecStatus.Success.name());
			instance.setLog(null);
			instanceList.add(instance);
		}
		instanceFlowService.batchInsert(instanceList);
	}

	@Test
	public void testCleanHistory(){
		instanceFlowService.cleanHistory(new Date());
	}
}
