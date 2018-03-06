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

import com.duowan.meteor.model.alarm.AlarmDataDelay;
import com.duowan.meteor.model.instance.InstanceDataDelay;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;
import com.duowan.meteor.transfer.util.AlertUtils;

public class AlarmDataDelayJobInstance {

	private static Logger logger = LoggerFactory.getLogger(AlarmDataDelayJobInstance.class);
	private Map<Integer, AlarmDataDelay> alarmDataDelayJobMap = new HashMap<Integer, AlarmDataDelay>();
	private String[] sendTypes = new String[] { "Mail", "YYPop", "SMS" };
	private String configName = "DataDelay_loadConfig";

	public static AlarmDataDelayJobInstance getInstatnce() {
		return AlarmDataDelayJobInstanceSingletonHolder.instance;
	}

	private static class AlarmDataDelayJobInstanceSingletonHolder {
		private static AlarmDataDelayJobInstance instance = new AlarmDataDelayJobInstance();
	}

	private AlarmDataDelayJobInstance() {
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
		List<AlarmDataDelay> alarmDataDelayList = ServiceConfigTool.alarmDataDelayService.getAllValid();
		if (alarmDataDelayList == null || alarmDataDelayList.size() == 0) {
			alarmDataDelayJobMap.clear();
			return;
		}
		Map<Integer, AlarmDataDelay> tmpAlarmDataDelayJobMap = new HashMap<Integer, AlarmDataDelay>();
		for (AlarmDataDelay alarmDataDelay : alarmDataDelayList) {
			tmpAlarmDataDelayJobMap.put(alarmDataDelay.getSourceTaskId(), alarmDataDelay);
		}
		alarmDataDelayJobMap = tmpAlarmDataDelayJobMap;
	}

	public void doAlarmDataDelay() {
		ServiceConfigTool.threadExecutor.submit(new Runnable() {
			@Override
			public void run() {
				String threaName = "DataDelay_execute";
				Thread.currentThread().setName(threaName);
				logger.info(threaName);
				try {
					alarmDataDelay();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
	}

	public void alarmDataDelay() {
		Date startCreateTime = DateUtils.addMinutes(new Date(), -5);
		List<InstanceDataDelay> instanceDataDelayList = ServiceConfigTool.instanceDataDelayService.getRecentDelays(startCreateTime);
		if (instanceDataDelayList == null || instanceDataDelayList.size() == 0) {
			return;
		}
		for (InstanceDataDelay instanceDataDelay : instanceDataDelayList) {
			AlarmDataDelay alarmDataDelay = alarmDataDelayJobMap.get(instanceDataDelay.getSourceTaskId());
			if (alarmDataDelay == null) {
				continue;
			}

			long factDelaySec = instanceDataDelay.getDelayMillis() / 1000;
			if (factDelaySec < alarmDataDelay.getDelaySecond()) {
				continue;
			}
			
			String alarmKey = "DataDelay_" + alarmDataDelay.getSourceTaskId();
			if (!RedisInstance.getInstatnce().isAlarm(alarmKey, alarmDataDelay.getAlarmGapMin())) {
				continue;
			}
			
			String taskName = "";
			try {
				AbstractBase task = ServiceConfigTool.taskService.getTask(alarmDataDelay.getSourceTaskId());
				taskName = task.getFileName();
			} catch (Exception e) {
				logger.error("", e);
			}
			String[] receivers = StringUtils.split(alarmDataDelay.getAlarmUsers(), ",");
			String msg = "meteor数据延迟: " + alarmDataDelay.getSourceTaskId() + ", " + taskName + ", " + factDelaySec + ">=" + alarmDataDelay.getDelaySecond();
			AlertUtils.alert(receivers, msg, msg, null, sendTypes);
		}
	}

}
