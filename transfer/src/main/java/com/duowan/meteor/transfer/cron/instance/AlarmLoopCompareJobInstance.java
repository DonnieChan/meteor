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

import com.duowan.meteor.model.alarm.AlarmLoopCompare;
import com.duowan.meteor.model.alarm.LoopRate;
import com.duowan.meteor.model.view.AbstractBase;
import com.duowan.meteor.transfer.tool.ServiceConfigTool;
import com.duowan.meteor.transfer.util.AlertUtils;

public class AlarmLoopCompareJobInstance {

	private static Logger logger = LoggerFactory.getLogger(AlarmLoopCompareJobInstance.class);
	private Map<Integer, AlarmLoopCompare> alarmLoopCompareMap = new HashMap<Integer, AlarmLoopCompare>();
	private String[] sendTypes = new String[] { "Mail", "YYPop", "SMS" };
	private int loopGapMin = 5;
	private String configName = "LoopCompare_loadConfig";

	public static AlarmLoopCompareJobInstance getInstatnce() {
		return AlarmLoopCompareJobInstanceSingletonHolder.instance;
	}

	private static class AlarmLoopCompareJobInstanceSingletonHolder {
		private static AlarmLoopCompareJobInstance instance = new AlarmLoopCompareJobInstance();
	}

	private AlarmLoopCompareJobInstance() {
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
		List<AlarmLoopCompare> alarmLoopCompareList = ServiceConfigTool.alarmLoopCompareService.getAllValid();
		if (alarmLoopCompareList == null || alarmLoopCompareList.size() == 0) {
			alarmLoopCompareMap.clear();
			return;
		}
		Map<Integer, AlarmLoopCompare> tmpAlarmLoopCompareMap = new HashMap<Integer, AlarmLoopCompare>();
		for (AlarmLoopCompare alarmLoopCompare : alarmLoopCompareList) {
			tmpAlarmLoopCompareMap.put(alarmLoopCompare.getSourceTaskId(), alarmLoopCompare);
		}
		alarmLoopCompareMap = tmpAlarmLoopCompareMap;
	}

	public void doAlarmLoopCompareJob() {
		ServiceConfigTool.threadExecutor.submit(new Runnable() {

			@Override
			public void run() {
				String threaName = "LoopCompare_execute";
				Thread.currentThread().setName(threaName);
				logger.info(threaName);
				try {
					alarmLoopCompareJob();
				} catch (Exception e) {
					logger.error("", e);
				}
			}
		});
	}

	private void alarmLoopCompareJob() {
		Date date2 = new Date();
		Date date1 = DateUtils.addMinutes(date2, 0 - loopGapMin);
		Date date0 = DateUtils.addMinutes(date1, 0 - loopGapMin);
		List<LoopRate> loopRateList = ServiceConfigTool.instanceFlowService.getLoopRates(date0, date1, date2);
		if (loopRateList == null || loopRateList.size() == 0) {
			return;
		}
		for (LoopRate loopRate : loopRateList) {
			AlarmLoopCompare alarmLoopCompare = alarmLoopCompareMap.get(loopRate.getSourceTaskId());
			if (alarmLoopCompare == null) {
				continue;
			}
			double factReduceRate = 1 - loopRate.getLoopRate().doubleValue();

			if (factReduceRate < alarmLoopCompare.getReduceRate().doubleValue()) {
				continue;
			}

			String alarmKey = "LoopCompare_" + alarmLoopCompare.getSourceTaskId();
			if (!RedisInstance.getInstatnce().isAlarm(alarmKey, alarmLoopCompare.getAlarmGapMin())) {
				continue;
			}

			String taskName = "";
			try {
				AbstractBase task = ServiceConfigTool.taskService.getTask(alarmLoopCompare.getSourceTaskId());
				taskName = task.getFileName();
			} catch (Exception e) {
				logger.error("", e);
			}
			String[] receivers = StringUtils.split(alarmLoopCompare.getAlarmUsers(), ",");
			String msg = "meteor流程环比下降: " + alarmLoopCompare.getSourceTaskId() + ", " + taskName + ", " + factReduceRate + ">=" + alarmLoopCompare.getReduceRate();
			AlertUtils.alert(receivers, msg, msg, null, sendTypes);
		}
	}

}
