package com.duowan.meteor.transfer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlertUtils {

	private static Logger logger = LoggerFactory.getLogger(AlertUtils.class);
	
	public static void alert(String[] receivers, String title, String msg, String url, String[] sendTypes) {
		logger.info("alert receivers={}, sendTypes={}, msg={}", new Object[] { receivers, sendTypes, msg });
		try {
			// TODO alert
		} catch (Exception e) {
			logger.error("alert error", e);
		}
	}
}
