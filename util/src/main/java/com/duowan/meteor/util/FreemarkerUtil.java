package com.duowan.meteor.util;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * Freemarker工具类
 * @author chenwu
 *
 */
public class FreemarkerUtil {

	/**
	 * 用Freemarker解析字符串脚本(默认参数和配置)
	 * @param script
	 * @return
	 */
	public static String parse(String script) {
		return parse(script, getDefaultParamMap(), getDefaultConf());
	}

	/**
	 * 用两次Freemarker解析字符串脚本
	 * @param script
	 * @param paramMap
	 * @return
	 */
	public static String parse(String script, Map<String, Object> paramMap) {
		Map<String, Object> finalParamMap = new HashMap<String, Object>();
		finalParamMap.putAll(getDefaultParamMap());
		finalParamMap.putAll(paramMap);
		return parse(script, finalParamMap, getDefaultConf());
	}
	
	/**
	 * 用Freemarker解析字符串脚本
	 * @param script
	 * @param paramMap
	 * @return
	 */
	public static String parse(String script, Map<String, Object> paramMap, Configuration conf) {
		Reader reader = null;
		Writer writer = null;
		String result;
		try {
			reader = new StringReader(script);
			Template template = new Template(""+reader, reader, conf);
			writer = new StringWriter();
			template.process(paramMap, writer);
			result = writer.toString();
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(writer);
			IOUtils.closeQuietly(reader);
		}
		return result;
	}
	
	/**
	 * 默认配置
	 * @return
	 */
	public static Configuration getDefaultConf() {
		Configuration conf = new Configuration();
		conf.setNumberFormat("###############");
		conf.setBooleanFormat("true,false");
		return conf;
	}
	
	/**
	 * 默认参数
	 * @return
	 */
	public static Map<String, Object> getDefaultParamMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("DateUtils", new DateUtils());
		params.put("DateUtils2", new DateUtils2());
		params.put("sysTime", new Date());
		return params;
	}
}
