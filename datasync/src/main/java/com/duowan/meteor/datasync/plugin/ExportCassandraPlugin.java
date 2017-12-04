package com.duowan.meteor.datasync.plugin;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.duowan.meteor.model.view.export.ExportCassandraToHiveTask;
import com.duowan.meteor.util.FreemarkerUtil;

/**
 * Created by huangshaoqian on 2015/11/12.
 */
public class ExportCassandraPlugin {

	private static Logger logger = LoggerFactory.getLogger(ExportCassandraPlugin.class);

	public static void exec(ExportCassandraToHiveTask task, Map<String, Object> params) throws Exception {
		long st = System.currentTimeMillis();
		logger.info("begin ExportCassandra");
		String cassandraHosts = (String) params.get("cassandraHosts");
		Cluster cluster = Cluster.builder().addContactPoints(StringUtils.split(cassandraHosts, ","))
				.withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE).setSerialConsistencyLevel(ConsistencyLevel.SERIAL))
				.withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy())).build();
		Session session = cluster.connect();
		String dataFile = (String) params.get("tmpDataPath") + "/cassandra.data";
		FileWriter writer = new FileWriter(dataFile);
		try {
			String sql = FreemarkerUtil.parse(task.getFetchSql(), params);
			logger.info("cassandra query sql: " + sql);
			ResultSet rs = session.execute(sql);
			Iterator<Row> it = rs.iterator();
			while (it.hasNext()) {
				Row row = it.next();
				StringBuffer sb = new StringBuffer();
				Map<String, Object> map = JSON.parseObject(row.getString(1));
				for (String column : task.getColumns().split(",")) {
					sb.append("\u0001");
					if (map.containsKey(column)) {
						sb.append(map.get(column));
					}
				}
				sb.delete(0, 1);
				writer.write(sb.toString());
			}
		} finally {
			writer.close();
			session.close();
			cluster.close();
			logger.info("ExportCassandra spendTime: " + (System.currentTimeMillis() - st));
		}
	}

}
