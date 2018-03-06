package com.duowan.meteor.util;

import java.util.Calendar;

public class DateUtils2 {
	
	public long expireAtDay(int addDay, int atHour, int atMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, addDay);
		calendar.set(Calendar.HOUR_OF_DAY, atHour);
		calendar.set(Calendar.MINUTE, atMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	public long expireAtHour(int addHour, int atMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, addHour);
		calendar.set(Calendar.MINUTE, atMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	public long expireAtMin(int addMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, addMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	public long expireAtWeek(int addWeek, int atWeekDay, int atHour, int atMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 7 * addWeek);
		calendar.set(Calendar.DAY_OF_WEEK, atWeekDay == 7 ? 1 : atWeekDay + 1);
		calendar.set(Calendar.HOUR_OF_DAY, atHour);
		calendar.set(Calendar.MINUTE, atMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	public long expireAtMonth(int addMonth, int atDay, int atHour, int atMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, addMonth);
		calendar.set(Calendar.DAY_OF_MONTH, atDay);
		calendar.set(Calendar.HOUR_OF_DAY, atHour);
		calendar.set(Calendar.MINUTE, atMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
}
