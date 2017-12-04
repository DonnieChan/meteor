package com.duowan.meteor.service.impl;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.duowan.meteor.model.custom.DefAllValid;
import com.duowan.meteor.model.view.other.Dir;
import com.duowan.meteor.service.TaskService;
import com.duowan.meteor.service.base.BaseServiceTestCase;

public class TaskServiceImplTest extends BaseServiceTestCase {

	private TaskService taskService;
	private String user = "dw_chenwu";
	private Date offlineTime = null;

	@Before
	public void setUp() throws ParseException {
		taskService = (TaskService) getBean("taskService");
		offlineTime = DateUtils.parseDate("2099-12-31", new String[] { "yyyy-MM-dd" });
	}
	
	@Test
	public void testGetDefAllValid() throws Exception {
		DefAllValid defAllValid = taskService.getDefAllValid();
		System.out.println(defAllValid);
	}
	
	
	@Test
	public void test_dir() throws Exception {
		Integer fileId = 50001;
		taskService.deleteTask(fileId);
		
		Dir task = new Dir();
		task.setFileId(fileId);
		task.setParentFileId(0);
		task.setProjectId(100);
		task.setFileName("虚拟任务");
		task.setContacts(user);
		task.setCreateUser(user);
		task.setUpdateUser(user);
		task.setOfflineTime(offlineTime);
		taskService.addTask(task);
		
		task = (Dir) taskService.getTask(fileId);
		System.out.println(task);
	}
}
