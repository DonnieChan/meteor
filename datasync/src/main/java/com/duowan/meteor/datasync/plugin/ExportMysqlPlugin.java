package com.duowan.meteor.datasync.plugin;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.util.DataSourceProvider;
import com.duowan.meteor.util.FreemarkerUtil;


public class ExportMysqlPlugin {
	private static Logger logger = LoggerFactory.getLogger(ExportMysqlPlugin.class);
	private final static String FIELD_SPLIT = "\u0001";

	public static void exec(String fetchSql, String columnsStr, String url, String user, String password, Map<String, Object> params) throws Exception {
		long execStartTime = System.currentTimeMillis();
		logger.info("begin ExportMysql");
		String sql = FreemarkerUtil.parse(fetchSql, params);
		logger.info("mysql execute sql: " + sql);
		Connection conn = null;
		ResultSet rs = null;
		Statement st = null;
		FileWriter writer = new FileWriter((String) params.get("tmpDataPath") + "/mysql.data");
		DataSource ds = DataSourceProvider.getDataSource("com.mysql.jdbc.Driver", url, user, password);
		try {
			conn = ds.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String[] columns = columnsStr.split(",");
			while (rs.next()) {
				StringBuffer line = new StringBuffer();
				for (int i = 0; i < columns.length; i++) {
					line.append(rs.getString(StringUtils.trim(columns[i]))).append(FIELD_SPLIT);
				}
				line.delete(line.length() - 1, line.length());
				line.append("\n");
				writer.write(line.toString());
			}
		} finally {
			writer.close();
			rs.close();
			st.close();
			conn.close();
			logger.info("ExportMysql spendTime: " + (System.currentTimeMillis() - execStartTime));
		}

	}

}
