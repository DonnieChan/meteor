package com.duowan.meteor.datasync.util;

import java.net.InetAddress;

public class MachineInfoUtil {

	public static String getLocalHostIP() {
		String ip;
		try {
			/** 返回本地主机。 */
			InetAddress addr = InetAddress.getLocalHost();
			/** 返回 IP 地址字符串（以文本表现形式） */
			ip = addr.getHostAddress();
		} catch (Exception ex) {
			ip = "";
		}
		return ip;
	}

	/**
	 * 或者主机名：
	 * 
	 * @return
	 */
	public static String getLocalHostName() {
		String hostName;
		try {
			/** 返回本地主机。 */
			InetAddress addr = InetAddress.getLocalHost();
			/** 获取此 IP 地址的主机名。 */
			hostName = addr.getHostName();
		} catch (Exception ex) {
			hostName = "";
		}
		return hostName;
	}
}
