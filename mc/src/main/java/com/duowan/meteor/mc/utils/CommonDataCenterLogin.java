package com.duowan.meteor.mc.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class CommonDataCenterLogin {
	public static final String KEY = "KnQoqmOphFbVuGj70JNE";

	public static boolean loginByDC(String passport, String time, String enc) {
		if (DigestUtils.md5Hex(passport + time + KEY).equals(enc)) {
			return true;
		}
		return false;
	}

	public static String getEnc(String passport, String time) {
		return DigestUtils.md5Hex(passport + time + KEY);
	}
}