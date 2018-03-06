package com.duowan.meteor.server.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 所有操作有sync版本，现在只使用同步版本
 * */
public class CuratorUtil {
	private static Log log = LogFactory.getLog(CuratorUtil.class);
	private static CuratorFramework curatorFramework;
	private static Map<PathChildrenCacheListener, PathChildrenCache> listeners;

	public static void init(String zkConnect,int sessionTimeout,String namespace) throws IOException {
		assert zkConnect != null;
		if (curatorFramework == null) {
			synchronized (CuratorUtil.class) {
				if (curatorFramework == null) {
					curatorFramework = CuratorFrameworkFactory.builder()
							.connectString(zkConnect).namespace(namespace)
							.retryPolicy(new RetryNTimes(10, 1000))
							.connectionTimeoutMs(sessionTimeout).build();
					curatorFramework.start();
					log.info("链接zookeeper:" + zkConnect);
					listeners = new HashMap<PathChildrenCacheListener, PathChildrenCache>();
				}
			}
		}
	}

	public static void createTmpNode(String path) throws Exception {
		createTmpNode(path, "");
	}

	public static void createTmpNode(String path, String data) throws Exception {
		curatorFramework.create().withMode(CreateMode.EPHEMERAL)
				.forPath(path, data.getBytes());
		log.info("创建临时节点：" + path);
	}

	public static void createPersist(String path) throws Exception {
		createPersistNode(path, "");
	}

	public static void createPersistNode(String path, String data)
			throws Exception {
		curatorFramework.create().withMode(CreateMode.PERSISTENT)
				.forPath(path, data.getBytes());
		log.info("创建节点：" + path);
	}

	public static void setData(String path, String data) throws Exception {
		curatorFramework.setData().forPath(path, data.getBytes());
	}

	public static boolean trySetData(String path, String data) throws Exception {
		boolean isSuc = true;
		try {
			curatorFramework.inTransaction().check().forPath(path).and()
					.setData().forPath(path, data.getBytes()).and().commit();
		} catch (NoNodeException ex) {
			isSuc = false;
		}
		return isSuc;
	}

	public static String getData(String path) throws Exception {
		assertPathExist(path);
		return new String(curatorFramework.getData().forPath(path));
	}

	public static String tryGetData(String path) throws Exception {
		String data = null;
		try {
			data = new String(curatorFramework.getData().forPath(path));
		} catch (NodeExistsException ex) {
			data = null;
		} catch (NoNodeException e) {
			data = null;
		}
		return data;
	}

	public static void createParentNodeIfNeed(String path) throws Exception {
		createParentNodeIfNeed(path, "");
	}

	public static void createParentNodeIfNeed(String path, String data)
			throws Exception {
		curatorFramework.create().creatingParentsIfNeeded().forPath(path);
	}

	public static void deleteNode(String path) throws Exception {
		assertPathExist(path);
		curatorFramework.delete().forPath(path);
		log.info("删除节点:" + path);
	}

	public static boolean tryDeleteNode(String path) {
		boolean isSuccess = true;
		try {
			curatorFramework.delete().forPath(path);
			log.info("删除节点:" + path);
		} catch (Exception e) {
			isSuccess = false;
		}
		return isSuccess;
	}

	public static void deleteNodeRecursively(String path) throws Exception {
		List<String> children = curatorFramework.getChildren().forPath(path);
		if (children != null) {
			for (String child : children)
				deleteNodeRecursively(path + "/" + child);
		}
		if (tryDeleteNode(path)) {
			log.debug("delete " + path);
		}
	}

	public static boolean isExist(String path) throws Exception {
		return curatorFramework.checkExists().forPath(path) != null;
	}

	private static void assertPathExist(String path) throws Exception {
		if (!isExist(path))
			throw new Exception("节点:" + path + "不存在");
	}

	public static boolean tryCreateTmpNode(String path, String data)
			throws Exception {
		boolean isExist = true;
		try {
			curatorFramework.inTransaction().create()
					.withMode(CreateMode.EPHEMERAL)
					.forPath(path, data.getBytes()).and().setData()
					.forPath(path, data.getBytes()).and().commit();
			log.info("创建临时节点：" + path);
		} catch (NodeExistsException ex) {
			isExist = false;
		}
		return isExist;
	}

	public static boolean tryCreateTmpNode(String path) throws Exception {
		return tryCreateTmpNode(path, "");
	}

	public static boolean tryCreatePersistNode(String path, String data)
			throws Exception {
		boolean isExist = true;
		try {
			createPersistNode(path, data);
		} catch (NodeExistsException ex) {
			isExist = false;
		}
		return isExist;
	}

	public static void createTmpNodesAtomic(String[] paths, String[] data)
			throws Exception {
		assert paths.length > 0;
		byte[] bytes = "".getBytes();
		CuratorTransaction createBuilder = curatorFramework.inTransaction();
		CuratorTransactionBridge transactionBridge = null;
		for (int i = 0; i < paths.length; i++) {
			if (transactionBridge == null) {
				transactionBridge = createBuilder
						.create()
						.forPath(paths[i])
						.and()
						.setData()
						.forPath(paths[i],
								data[i] == null ? bytes : data[i].getBytes());
			} else {
				transactionBridge = transactionBridge
						.and()
						.create()
						.forPath(paths[i])
						.and()
						.setData()
						.forPath(paths[i],
								data[i] == null ? bytes : data[i].getBytes());
			}
		}
		transactionBridge.and().commit();
	}

	public static List<String> getChildren(String path) throws Exception {
		assertPathExist(path);
		List<String> children = curatorFramework.getChildren().forPath(path);
		return children;
	}


	/**
	 * 监控namespace下某个path的孩子节点变化
	 * */
	public static void registerListenerOnChildren(String path,
			PathChildrenCacheListener pathChildrenCacheListener)
			throws Exception {
		PathChildrenCache pathChildrenCache = new PathChildrenCache(
				curatorFramework, path, true);
		pathChildrenCache.getListenable()
				.addListener(pathChildrenCacheListener);
		pathChildrenCache.start();
		listeners.put(pathChildrenCacheListener, pathChildrenCache);
	}

	/**
	 * 监控namespace下某个path的孩子节点变化
	 * */
	public static void registerConnectionStateListener(
			ConnectionStateListener connectionStateListener) throws Exception {
		curatorFramework.getConnectionStateListenable().addListener(
				connectionStateListener);
	}

	/**
	 * 监控namespace下某个path的孩子节点变化
	 * */
	public static void removeListenerOnChildren(
			PathChildrenCacheListener pathChildrenCacheListener) {
		PathChildrenCache pathChildrenCache = listeners
				.get(pathChildrenCacheListener);
		try {
			if (pathChildrenCache != null)
				pathChildrenCache.close();
		} catch (IllegalStateException e) {
			// 忽略
		} catch (IOException ex) {
		}
	}

	public static void close() {
		curatorFramework.close();
	}

	public static String getPathShortName(String path) {
		if (path != null && path.contains("/")) {
			return path.substring(path.lastIndexOf("/") + 1);
		}
		return null;
	}

}
