package com.duowan.meteor.transfer.cron.instance;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.model.alarm.AlarmError;
import com.duowan.meteor.model.enumtype.ExecStatus;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;
import com.duowan.meteor.transfer.util.AlertUtils;


public class AlarmErrorJobInstance {
	
	private static Logger logger = LoggerFactory.getLogger(AlarmErrorJobInstance.class);
	private Map<Integer, AlarmError> alarmErrorMap = new HashMap<Integer, AlarmError>();
	private String[] sendTypes = new String[] { "Mail", "YYPop", "SMS" };
	private String configName = "Error_loadConfig";
	
	public static AlarmErrorJobInstance getInstatnce() {
		return AlarmErrorJobInstanceSingletonHolder.instance;
	}
	
	private static class AlarmErrorJobInstanceSingletonHolder {
		private static AlarmErrorJobInstance instance = new AlarmErrorJobInstance();
	}
	
	private AlarmErrorJobInstance() {
		super();
		loadAlarmConfig();
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName(configName);
				try {
					loadAlarmConfig();
				} catch(Exception e) {
					logger.error("", e);
				}
			}
			
		}, 5, 5, TimeUnit.MINUTES);
	}

	private void loadAlarmConfig() {
		logger.info(configName);
		List<AlarmError> alarmErrorList = ServiceConfigTool.alarmErrorService.getAllValid();
		if (alarmErrorList == null || alarmErrorList.size() == 0) {
			alarmErrorMap.clear();
			return;
		}
		Map<Integer, AlarmError> tmpAlarmErrorMap = new HashMap<Integer, AlarmError>();
		for (AlarmError alarmError : alarmErrorList) {
			tmpAlarmErrorMap.put(alarmError.getSourceTaskId(), alarmError);
		}
		alarmErrorMap = tmpAlarmErrorMap;
	}
	
	public void doAlarmError() {
		ServiceConfigTool.threadExecutor.submit(new Runnable() {

			@Override
			public void run() {
				String threaName = "Error_execute";
				Thread.currentThread().setName(threaName);
				logger.info(threaName);
				try {
					alarmError();
				} catch(Exception e) {
					logger.error("", e);
				}
			}
			
		});
	}

	public void alarmError() {
		Date startCreateTime = DateUtils.addMinutes(new Date(), -5);
		List<InstanceFlow> instanceFlowList = ServiceConfigTool.instanceFlowService.getRecentInstances(startCreateTime, ExecStatus.Fail.name());
		if (instanceFlowList == null || instanceFlowList.size() == 0) {
			return;
		}
		for (InstanceFlow instanceFlow : instanceFlowList) {
			AlarmError alarmError = alarmErrorMap.get(instanceFlow.getSourceTaskId());
			if (alarmError == null) {
				continue;
			}
			
			String alarmKey = "Error_" + alarmError.getSourceTaskId();
			if (!RedisInstance.getInstatnce().isAlarm(alarmKey, alarmError.getAlarmGapMin())) {
				continue;
			}
			
			String taskName = "";
			try {
				AbstractBase task = ServiceConfigTool.taskService.getTask(alarmError.getSourceTaskId());
				taskName = task.getFileName();
			} catch (Exception e) {
				logger.error("", e);
			}
			String[] receivers = StringUtils.split(alarmError.getAlarmUsers(), ",");
			String msg = "meteor流程运行报错: " + alarmError.getSourceTaskId() + ", " + taskName;
			AlertUtils.alert(receivers, msg, msg, null, sendTypes);
		}
	}
	
}
