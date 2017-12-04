package com.duowan.meteor.datasync.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cassandra.io.sstable.CQLSSTableWriter;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.DCAwareRoundRobinPolicy;
import com.datastax.driver.core.policies.TokenAwarePolicy;
import com.duowan.meteor.datasync.util.FileHelper;
import com.duowan.meteor.datasync.util.JmxBulkLoader;
import com.duowan.meteor.model.view.importcassandra.ImportCassandraTask;

/**
 * 
 * @author chenwu
 *
 */
public class ImportCassandraPlugin {

	private static Logger logger = LoggerFactory.getLogger(ImportCassandraPlugin.class);

	public static void exec(final ImportCassandraTask task, Map<String, Object> params) throws Exception {
		long execStartTime = System.currentTimeMillis();
		logger.info("begin ImportCassandra");
		String cassandraHosts = (String) params.get("cassandraHosts");
		Cluster cluster = Cluster.builder().addContactPoints(StringUtils.split(cassandraHosts, ","))
				.withQueryOptions(new QueryOptions().setConsistencyLevel(ConsistencyLevel.ONE).setSerialConsistencyLevel(ConsistencyLevel.SERIAL))
				.withLoadBalancingPolicy(new TokenAwarePolicy(new DCAwareRoundRobinPolicy())).build();
		Session session = cluster.connect();

		String creatKeySpace = String.format("create keyspace if not exists %s WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};", task.getKeySpace());
		session.execute(creatKeySpace);
		String createTable = String
				.format("CREATE TABLE IF NOT EXISTS %s.%s (key text, value text, primary key(key)) WITH bloom_filter_fp_chance=0.1 AND compaction={'class': 'LeveledCompactionStrategy'} AND caching={'keys':'ALL', 'rows_per_partition':'NONE'}",
						task.getKeySpace(), task.getTable());
		session.execute(createTable);

		session.close();
		cluster.close();

		String insertStatement = String.format("insert into %s.%s (key, value) values (?, ?)", task.getKeySpace(), task.getTable());
		logger.info("insert statement: " + insertStatement);
		final CQLSSTableWriter.Builder builder = CQLSSTableWriter.builder().forTable(createTable).withBufferSizeInMB(64).using(insertStatement);

		String baseDir = (String) params.get("tmpDataPath");
		List<File> fileList = FileHelper.listFiles(baseDir);
		ExecutorService es = Executors.newCachedThreadPool();
		if (fileList != null && fileList.size() > 0) {
			int fileNum = fileList.size();
			logger.info("fileNum : " + fileNum);
			final CountDownLatch counter = new CountDownLatch(fileNum);
			final List<Exception> exceptionList = new ArrayList<Exception>();
			for (final File file : fileList) {
				es.submit(new Runnable() {
					@Override
					public void run() {
						try {
							String directory = file.getAbsolutePath() + "_dir/" + task.getKeySpace() + "/" + task.getTable() + "/";
							new File(directory).mkdirs();
							CQLSSTableWriter writer = builder.inDirectory(directory).build();
							BufferedReader iReader = new BufferedReader(new FileReader(file));
							String line = iReader.readLine();
							String[] columns = task.getColumns().split(",");
							while (line != null) {
								String[] strs = line.split("\u0001");
								Map<String, String> row = new HashMap<String, String>();
								for (int i = 0; i < strs.length; i++) {
									if (StringUtils.isNotBlank(strs[i]) && (!"\\N".equals(strs[i].trim()))) {
										row.put(columns[i].toLowerCase().trim(), strs[i]);
									}
								}
								Object srow = JSON.toJSON(row);
								writer.addRow(new Object[] { getTableKey(task, row), srow.toString() });
								line = iReader.readLine();
							}
							iReader.close();
							writer.close();
							syncToCluster(directory);
						} catch (Exception e) {
							exceptionList.add(e);
							while(counter.getCount()>0) {
								counter.countDown();
							}
						} finally {
							counter.countDown();
						}
					}
				});
			}
			counter.await();
			if(exceptionList.size() > 0) {
				throw exceptionList.get(0);
			}
		}
		logger.info("finish import cassandra, duration: " + (System.currentTimeMillis() - execStartTime));
	}

	/**
	 * get table key
	 * 
	 * @param row
	 * @return
	 */
	private static String getTableKey(ImportCassandraTask task, Map<String, String> row) {
		StringBuffer sb = new StringBuffer();
		for (String key : task.getTableKeys().split(",")) {
			sb.append(row.get(key)).append("|");
		}
		sb.delete(sb.length() - 1, sb.length());
		return sb.toString();
	}

	private static void syncToCluster(String directory) throws Exception {
		long execStartTime = System.currentTimeMillis();
		logger.info("Running Bulk Load via JMX, load directory: " + directory);
		JmxBulkLoader jmxLoader = new JmxBulkLoader("127.0.0.1", 7199);
		jmxLoader.bulkLoad(directory);
		logger.info("Finished Bulk Load in. directory: " + directory + ", duration: " + (System.currentTimeMillis() - execStartTime));
		jmxLoader.close();
	}

}