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

import com.duowan.meteor.model.alarm.AlarmSliceDelay;
import com.duowan.meteor.model.instance.InstanceFlow;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;
import com.duowan.meteor.transfer.util.AlertUtils;

public class AlarmSliceDelayJobInstance {

	private static Logger logger = LoggerFactory.getLogger(AlarmSliceDelayJobInstance.class);
	private Map<Integer, AlarmSliceDelay> alarmSliceDelayMap = new HashMap<Integer, AlarmSliceDelay>();
	private String[] sendTypes = new String[] { "Mail", "YYPop", "SMS" };
	private String configName = "SliceDelay_loadConfig";

	public static AlarmSliceDelayJobInstance getInstatnce() {
		return AlarmSliceDelayJobInstanceSingletonHolder.instance;
	}

	private static class AlarmSliceDelayJobInstanceSingletonHolder {
		private static AlarmSliceDelayJobInstance instance = new AlarmSliceDelayJobInstance();
	}

	public AlarmSliceDelayJobInstance() {
		super();
		loadAlarmConfig();
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Thread.currentThread().setName(configName);
				try {
					loadAlarmConfig();
				} catch (Exception e) {
					logger.error("", e);
				}
			}

		}, 5, 5, TimeUnit.MINUTES);
	}

	private void loadAlarmConfig() {
		logger.info(configName);
		List<AlarmSliceDelay> alarmSliceDelayList = ServiceConfigTool.alarmSliceDelayService.getAllValid();
		if (alarmSliceDelayList == null || alarmSliceDelayList.size() == 0) {
			alarmSliceDelayMap.clear();
			return;
		}
		Map<Integer, AlarmSliceDelay> tmpAlarmSliceDelayMap = new HashMap<Integer, AlarmSliceDelay>();
		for (AlarmSliceDelay alarmSliceDelay : alarmSliceDelayList) {
			tmpAlarmSliceDelayMap.put(alarmSliceDelay.getSourceTaskId(), alarmSliceDelay);
		}
		alarmSliceDelayMap = tmpAlarmSliceDelayMap;
	}

	public void doAlarmSliceDelay() {
		ServiceConfigTool.threadExecutor.submit(new Runnable() {

			@Override
			public void run() {
				String threaName = "SliceDelay_execute";
				Thread.currentThread().setName(threaName);
				logger.info(threaName);
				try {
					alarmSliceDelay();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
	}

	private void alarmSliceDelay() {
		Date startCreateTime = DateUtils.addMinutes(new Date(), -5);
		List<InstanceFlow> instanceFlowList = ServiceConfigTool.instanceFlowService.getRecentInstances(startCreateTime, null);
		if (instanceFlowList == null || instanceFlowList.size() == 0) {
			return;
		}
		for (InstanceFlow instanceFlow : instanceFlowList) {
			AlarmSliceDelay alarmSliceDelay = alarmSliceDelayMap.get(instanceFlow.getSourceTaskId());
			if (alarmSliceDelay == null) {
				continue;
			}

			long spendSec = (instanceFlow.getEndTime().getTime() - instanceFlow.getStartTime().getTime()) / 1000;
			if (spendSec <= alarmSliceDelay.getDelaySecond()) {
				continue;
			}

			String alarmKey = "SliceDelay_" + alarmSliceDelay.getSourceTaskId();
			if (!RedisInstance.getInstatnce().isAlarm(alarmKey, alarmSliceDelay.getAlarmGapMin())) {
				continue;
			}

			String taskName = "";
			try {
				AbstractBase task = ServiceConfigTool.taskService.getTask(alarmSliceDelay.getSourceTaskId());
				taskName = task.getFileName();
			} catch (Exception e) {
				logger.error("", e);
			}
			String[] receivers = StringUtils.split(alarmSliceDelay.getAlarmUsers(), ",");
			String msg = "meteor流程运行过慢: " + alarmSliceDelay.getSourceTaskId() + ", " + taskName + ", " + spendSec + ">" + alarmSliceDelay.getDelaySecond();
			AlertUtils.alert(receivers, msg, msg, null, sendTypes);
		}
	}

}
