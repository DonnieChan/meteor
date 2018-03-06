package com.duowan.meteor.util;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

public class DateUtils2Test {

	@Test
	public void test() throws Exception {
		long result = expireAtDay(1, 1, 30);
		long time = System.currentTimeMillis() + result * 1000;
		Date date = new Date(time);
		System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
	}
	
	public long expireAtDay(int addDay, int atHour, int atMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, addDay);
		calendar.set(Calendar.HOUR_OF_DAY, atHour);
		calendar.set(Calendar.MINUTE, atMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	@Test
	public void test2() throws Exception {
		long result = expireAtHour(2, 0);
		long time = System.currentTimeMillis() + result * 1000;
		Date date = new Date(time);
		System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
	}
	
	public long expireAtHour(int addHour, int atMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, addHour);
		calendar.set(Calendar.MINUTE, atMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	@Test
	public void test3() throws Exception {
		long result = expireAtMin(30);
		long time = System.currentTimeMillis() + result * 1000;
		Date date = new Date(time);
		System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
	}
	
	public long expireAtMin(int addMin) {
		long curTime = System.currentTimeMillis();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, addMin);
		long result = (calendar.getTimeInMillis() - curTime) / 1000;
		return result;
	}
	
	@Test
	public void test4() throws Exception {
		long result = expireAtWeek(0, 1, 1, 30);
		long time = System.currentTimeMillis() + result * 1000;
		Date date = new Date(time);
		System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
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
	
	@Test
	public void test5() throws Exception {
		long result = expireAtMonth(1, 1, 1, 30);
		long time = System.currentTimeMillis() + result * 1000;
		Date date = new Date(time);
		System.out.println(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
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
