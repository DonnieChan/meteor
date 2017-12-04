package com.duowan.meteor.datasync.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.service.StorageServiceMBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by haungshaoqian on 2015/10/9
 */
public class JmxBulkLoader {

	private static Logger logger = LoggerFactory.getLogger(JmxBulkLoader.class);

	private JMXConnector connector;
	private StorageServiceMBean storageBean;

	public JmxBulkLoader(String host, int port) throws Exception {
		connect(host, port);
	}

	private void connect(String host, int port) throws IOException, MalformedObjectNameException {
		JMXServiceURL jmxUrl = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi", host, port));

		Map<String, Object> env = new HashMap<String, Object>();
		connector = JMXConnectorFactory.connect(jmxUrl, env);
		MBeanServerConnection mbeanServerConn = connector.getMBeanServerConnection();

		ObjectName name = new ObjectName("org.apache.cassandra.db:type=StorageService");
		storageBean = JMX.newMBeanProxy(mbeanServerConn, name, StorageServiceMBean.class);
	}

	public void close() throws IOException {
		connector.close();
	}

	public void bulkLoad(String path) {
		storageBean.bulkLoad(path);
		purgeDirectory(new File(path));
	}

	public void purgeDirectory(File dir) {
		logger.info("Purging directory " + dir.getAbsolutePath());
		for (File file : dir.listFiles()) {
			if (file.isDirectory())
				purgeDirectory(file);
			file.delete();
		}
	}
}