package com.duowan.meteor.transfer.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.meteor.transfer.MeteorTransfer;


/**
 * 随web应用启动而启动
 * @author chenwu
 */
public class InitTransferListener implements ServletContextListener {
	
	private static Logger logger = LoggerFactory.getLogger(InitTransferListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("startup transfer");
		try {
			MeteorTransfer.startup();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		logger.info("endup transfer");
		try {
			MeteorTransfer.endup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
