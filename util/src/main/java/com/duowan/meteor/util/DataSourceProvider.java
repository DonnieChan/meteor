package com.duowan.meteor.util;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * 数据源提供器
 *
 */
public class DataSourceProvider {

	private static Logger logger = Logger.getLogger(DataSourceProvider.class);
	private static Map<String, DataSource> dataSourceCache = new HashMap<String, DataSource>();

	public static synchronized DataSource getDataSource(String jdbcDriver, String jdbcUrl, String jdbcUsername, String jdbcPassword) {
		String key = jdbcDriver + jdbcUrl + jdbcUsername + jdbcPassword;
		DataSource dataSource = dataSourceCache.get(key);
		if (dataSource == null) {
			try {
				ComboPooledDataSource ds = new ComboPooledDataSource();
				ds.setDriverClass(jdbcDriver);
				ds.setJdbcUrl(jdbcUrl);
				ds.setUser(jdbcUsername);
				ds.setPassword(jdbcPassword);

				ds.setUnreturnedConnectionTimeout(60);
				ds.setMaxStatementsPerConnection(60);
				ds.setMaxStatements(180);

				ds.setMinPoolSize(5);
				ds.setInitialPoolSize(5);
				ds.setMaxPoolSize(15);
				ds.setMaxIdleTime(900);
				ds.setCheckoutTimeout(30000);

				dataSource = ds;
				dataSourceCache.put(key, dataSource);
				logger.info("create DataSource, url:[" + jdbcUrl + "], username:" + jdbcUsername);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return dataSource;
	}

}
