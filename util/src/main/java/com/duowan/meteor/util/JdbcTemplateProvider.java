package com.duowan.meteor.util;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * jdbcTemplate提供器
 *
 */
public class JdbcTemplateProvider {

	private static Logger logger = Logger.getLogger(JdbcTemplateProvider.class);
	private static Map<String, NamedParameterJdbcTemplate> jdbcTemplateCache = new HashMap<String, NamedParameterJdbcTemplate>();

	public static NamedParameterJdbcTemplate getJdbcTemplate(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
		String key = jdbcDriver + jdbcUrl + jdbcUsername + jdbcPassword;
		NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplateCache.get(key);
		if (jdbcTemplate == null) {
			jdbcTemplate = initJdbcTemplate(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
		}
		return jdbcTemplate;
	}

	public static synchronized NamedParameterJdbcTemplate initJdbcTemplate(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
		String key = jdbcDriver + jdbcUrl + jdbcUsername + jdbcPassword;
		NamedParameterJdbcTemplate jdbcTemplate = jdbcTemplateCache.get(key);
		if (jdbcTemplate == null) {
			logger.info("create jdbcTemplate, url:[" + jdbcUrl + "], username:" + jdbcUsername);
			DataSource ds = DataSourceProvider.getDataSource(jdbcDriver, jdbcUrl, jdbcUsername, jdbcPassword);
			jdbcTemplate = new NamedParameterJdbcTemplate(ds);
			jdbcTemplateCache.put(key, jdbcTemplate);
		}
		return jdbcTemplate;
	}
}
