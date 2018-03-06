package com.duowan.meteor.util;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * apache commons-exec，可执行shell
 * @author chenwu
 *
 */
public class ApacheCommonsExecutor {

	private static Logger logger = LoggerFactory.getLogger(ApacheCommonsExecutor.class);
	
	public static boolean exec(String program) {
		return exec(program, System.out, System.err);
	}
	
	public static boolean exec(String program, OutputStream stdOut, OutputStream errorOut) {
		logger.info("program = " + program);
        DefaultExecutor exec = new DefaultExecutor();  
		CommandLine commandline = CommandLine.parse(program);    
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdOut, errorOut);
		exec.setStreamHandler(streamHandler);
		int exitValue = 0;
		try {
			exitValue = exec.execute(commandline);
		} catch (ExecuteException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if( exitValue == 0 ) {
			logger.info("Successfully execute");
        	return true;
		}
		logger.info("Falsely execute");
		return false;
    }
}
