package com.duowan.meteor.mc.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.model.db.DefFileSys;
import com.duowan.meteor.model.enumtype.ExecStatus;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.instance.InstanceTaskDB;
import com.duowan.meteor.model.query.InstanceFlowQuery;
import com.duowan.meteor.model.query.InstanceTaskQuery;
import com.duowan.meteor.service.DefFileSysService;
import com.duowan.meteor.service.InstanceFlowService;
import com.duowan.meteor.service.InstanceTaskService;

/**
 * 实例控制器
 * 
 * @author liuchaohong
 *
 */
@Controller
public class InstanceController {

	private final static int pageCount = 30;

	@Autowired
	private InstanceFlowService instanceFlowService;
	@Autowired
	private InstanceTaskService instanceTaskService;
	@Autowired
	private DefFileSysService defFileSysService;

	@RequestMapping("/instanceFlow.do")
	public String instanceFlow(InstanceFlowQuery query, Integer pageNumber, ModelMap model) throws Exception {
		Assert.notNull(query.getSourceTaskId(), "Wrong!! sourceTaskId must not be null!");

		Date date = new Date();
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		if (StringUtils.isBlank(query.getEndTime())) {
			query.setEndTime(DateFormatUtils.format(date, dateFormat));
		}
		if (StringUtils.isBlank(query.getStartTime())) {
			query.setStartTime(DateFormatUtils.format(DateUtils.addMinutes(date, -60), dateFormat));
		}

		List<InstanceFlow> allInstanceFlows = instanceFlowService.getByQuery(query);
		int firstIndex = getFirstIndex(pageNumber, allInstanceFlows.size(), model);
		query.setFirstIndex(firstIndex);
		query.setPageCount(pageCount);
		List<InstanceFlow> instanceFlows = instanceFlowService.getByQuery(query);
		DefFileSys defFileSys = defFileSysService.getById(query.getSourceTaskId());

		model.put("instanceFlows", instanceFlows);
		model.put("instanceFlowQuery", query);
		model.put("defFileSys", defFileSys);

		return "pages/home/instance_flow";
	}

	/**
	 * 流程实例任务汇总页面
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/instanceFlowTask.do")
	public String instanceFlowTask(String instanceFlowId, Long duration, ModelMap model) throws Exception {
		InstanceTaskQuery query = new InstanceTaskQuery();
		query.setInstanceFlowId(instanceFlowId);
		List<InstanceTaskDB> instanceTasks = instanceTaskService.getByQuery(query);
		int allInstanceTaskSize = 0;
		int sucessInstanceTaskSize = 0;
		int failInstanceTaskSize = 0;
		if (instanceTasks != null) {
			for (InstanceTaskDB instanceTask : instanceTasks) {
				allInstanceTaskSize++;
				if (StringUtils.equals(instanceTask.getStatus(), ExecStatus.Success.name())) {
					sucessInstanceTaskSize++;
				} else if (StringUtils.equals(instanceTask.getStatus(), ExecStatus.Fail.name())) {
					failInstanceTaskSize++;
				}
				DefFileSys defFileSys = defFileSysService.getById(instanceTask.getFileId());
				instanceTask.setFileName(defFileSys.getFileName());
			}
		}

		model.put("duration", duration);
		model.put("allInstanceTaskSize", allInstanceTaskSize);
		model.put("sucessInstanceTaskSize", sucessInstanceTaskSize);
		model.put("failInstanceTaskSize", failInstanceTaskSize);
		model.put("instanceTasks", instanceTasks);
		return "pages/home/instance_flow_task";
	}

	@RequestMapping("/instanceTask.do")
	public String instanceTask(InstanceTaskQuery query, Integer pageNumber, ModelMap model) throws Exception {
		Assert.notNull(query.getFileId(), "Wrong!! fileId must not be null!");

		Date date = new Date();
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		if (StringUtils.isBlank(query.getEndTime())) {
			query.setEndTime(DateFormatUtils.format(date, dateFormat));
		}
		if (StringUtils.isBlank(query.getStartTime())) {
			query.setStartTime(DateFormatUtils.format(DateUtils.addMinutes(date, -60), dateFormat));
		}

		List<InstanceTaskDB> allInstanceTasks = instanceTaskService.getByQuery(query);
		int firstIndex = getFirstIndex(pageNumber, allInstanceTasks.size(), model);
		query.setFirstIndex(firstIndex);
		query.setPageCount(pageCount);
		List<InstanceTaskDB> instanceTasks = instanceTaskService.getByQuery(query);
		DefFileSys defFileSys = defFileSysService.getById(query.getFileId());

		model.put("instanceTasks", instanceTasks);
		model.put("instanceTaskQuery", query);
		model.put("defFileSys", defFileSys);

		return "pages/home/instance_task";
	}

	/**
	 * 流程任务日志页面
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping("/instanceTaskLog.do")
	public String instanceTaskLog(InstanceTaskQuery query, ModelMap model) {
		Integer fileId = query.getFileId();
		String instanceFlowId = query.getInstanceFlowId();
		InstanceTaskDB instanceTask = instanceTaskService.getById(instanceFlowId, fileId);
		instanceTask.setFileBody(StringUtils.replace(instanceTask.getFileBody(), "\\r\\n", "\n"));
		model.put("instanceTask", instanceTask);
		return "pages/home/instance_task_log";
	}

	/**
	 * 根据页数获取头索引，并设置页面参数
	 * 
	 * @param pageNumber
	 * @param totalRecord
	 * @param model
	 * @return
	 */
	private Integer getFirstIndex(Integer pageNumber, Integer totalRecord, ModelMap model) {
		pageNumber = pageNumber == null ? 1 : pageNumber;
		int pageNow = pageNumber;
		int pageBack = pageNumber - 1;
		int pageNext = pageNumber + 1;
		int firstIndex = pageCount * (pageNumber - 1);
		int pageMax = totalRecord % pageCount == 0 ? totalRecord / pageCount : totalRecord / pageCount + 1;

		model.put("pageNow", pageNow);
		model.put("pageBack", pageBack);
		model.put("pageNext", pageNext);
		model.put("firstIndex", firstIndex);
		model.put("pageMax", pageMax);
		return firstIndex;
	}

}
