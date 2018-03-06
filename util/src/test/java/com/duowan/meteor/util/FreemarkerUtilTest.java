package com.duowan.meteor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class FreemarkerUtilTest {

	@Test
	public void testParse() {
		String script = "meteor.dwd_pay_consume_${sysTime?string('yyyyMMdd')}";
		String result = FreemarkerUtil.parse(script);
		System.out.println(result);
		// Assert.assertTrue(StringUtils.equals("dw_xx_20151015", result));

		script = "xx,xx,${DateUtils.addDays(sysTime, -1)?string('yyyyMMddHH')}";
		result = FreemarkerUtil.parse(script);
		System.out.println(result);
		// Assert.assertTrue(StringUtils.equals("xx,xx,2015101409", result));
	}

	@Test
	public void test1() {
		String sql = "select stime, game, game_server, hincrby('gas_po_new_user_o5_gs_ucnt_his', concat(game, '|', game_server), 0) + hincrby('gas_po_new_user_o5_gs_ucnt_${DateUtils.addDays(sysTime, -1)?string('yyyyMMdd')}', concat(game, '|', game_server), 0) + hincrby('123', 0, '', 1, 'gas_po_new_user_o5_gs_ucnt_${sysTime?string('yyyyMMdd')}', concat(game, '|', game_server), 1) AS new_user_ucnt from dw_game_server_online5min_new_user_gs";
		String pattern = "(?i)hincrby\\(\\'([^\\s\\,\\']+)\\'\\s*\\,\\s*([^\\s\\,\\']+)\\s*\\,\\s*([^\\s\\,]+)\\s*\\,\\s*([^\\s\\,\\']+)\\s*\\,\\s*\\'([^\\s\\,\\$\\{]+\\$\\{[^\\}]+\\})\\'\\s*\\,|(?i)hincrby\\(\\'([^\\s\\,\\$\\{]+\\$\\{[^\\}]+\\})\\'\\s*\\,";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(sql);
		String result = "";
		while (m.find()) {
			result = m.group(1) + ", " + m.group(2) + ", " + m.group(3) + ", " + m.group(4) + ", " + m.group(5) + ", " + m.group(6);
			System.out.println(result);
		}
	}

}
