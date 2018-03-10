package com.duowan.meteor.jetty.server;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class JettyServer {

	/**
	 * java -Xms512m -Xmx512m -cp /data/apps/meteor/meteor-jetty-server-1.0-SNAPSHOT-jar-with-dependencies.jar com.duowan.meteor.jetty.server.JettyServer "/data/apps/meteor/meteor-rtview-1.0-SNAPSHOT.war" "/" "8888"
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Server server = buildNormalServer(args[0], args[1], Integer.parseInt(args[2]));
		server.start();
	}
	
	/**
	 * 创建用于正常运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 */
	public static Server buildNormalServer(String warPath, String contextPath, int port) {
		Server server = new Server(port);
		WebAppContext webContext = new WebAppContext();
		webContext.setContextPath(contextPath);
		webContext.setWar(warPath);
		server.setHandler(webContext);
		server.setStopAtShutdown(true);
		return server;
	}
}
