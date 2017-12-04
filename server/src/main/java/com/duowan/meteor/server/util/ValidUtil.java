package com.duowan.meteor.server.util;

import org.apache.commons.lang.StringUtils;

public class ValidUtil {

	public static boolean isValidString(String str) {
		if(StringUtils.isBlank(str)) {
			return true;
		}
		int usernameLen = str.length();
		for (int i = 0; i < usernameLen; i++) {
			char c = str.charAt(i);
			if (c <= 32) {
				return false;
			}
			if(c == 127) {
				return false;
			}
			
			if ('　' == c) {
				// 全角空格不视为空格
				continue;
			}
		}
		
		return true;
	}
}
