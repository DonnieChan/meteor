package com.duowan.meteor.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

public class Log4jConfUtils {

	public static void resetConf(String logConfPath){
        org.apache.log4j.LogManager.resetConfiguration();
        Properties logProperties = new Properties();
        try {
            logProperties.load(new FileInputStream(new File(logConfPath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        PropertyConfigurator.configure(logProperties);
    }
}
