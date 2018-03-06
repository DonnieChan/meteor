package com.duowan.meteor.server.udf;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * 
 * 描述：切分层次
 * 
 * 函数原型：get_levels
 * 
 * 参数说明: input：String，输入字符串；level：int，切分层次
 * 如level为负数，则返回输入数据的层级数
 * 
 * 返回值: 切分后层次字符串
 * 
 * 使用例子：
	getLevels("login/load_left/load_left_left",1) >> login
    getLevels("login/load_left/load_left_left",2) >> login/load_left
    getLevels("login/load_left/load_left_left",3) >> login/load_left/load_left_left
    getLevels("login/load_left/load_left_left",-1) >> 3
 * 
 * 
 * 
 */
public class UDFGetLevels {

	private static final char DEFAULT_LEVEL_SEPARATOR_CHAR = '/';

	public static String evaluate(String input ,int level) {
		String[] al = StringUtils.split(input,DEFAULT_LEVEL_SEPARATOR_CHAR);
		if(al == null || al.length < level ) {
			return null;
		}
		if(level<=0){
			return "" +  al.length;
		}
		String[] result = new String[level];
		System.arraycopy(al, 0, result, 0, level);
		return StringUtils.join(result,'/');
	}
	
	/**
	 * path1///path2/// ==> path1/path2
	 * path1/path2/// ==> path1/path2
	 * path1/path2/ ==> path1/path2
	 * path1/path2  ==> path1/path2
	 * 
	 * @param input     格式化输入：raw 原生的，即返回本身（删除误报的后缀分隔符 如：“/”）。
	 * @return
	 */
	public static String evaluate(String input) {
		if(StringUtils.isBlank(input)) {
			return null;
		}
		String[] al = StringUtils.split(input,DEFAULT_LEVEL_SEPARATOR_CHAR);
		List<String> resultList = new ArrayList<String>();
		for(String a : al){
			if(StringUtils.isNotBlank(a)){
				resultList.add(a) ; 
			}
		}
		if(resultList.isEmpty()) {
			return null;
		}
		return StringUtils.join(resultList,'/');
	}
	
	
	/**  get_levels('a/b/c',1,3)='b/c' */
	public static String evaluate(String input ,int start,int end) {
		if(input == null || start < 0 || end < 0 || start >= end){
			return null;
		}
		if(StringUtils.isBlank(input)){
			return "";
		}
		String[] al = StringUtils.split(input,DEFAULT_LEVEL_SEPARATOR_CHAR);
		List<String> resultList = new ArrayList<String>();
		for(String a : al){
			if(StringUtils.isNotBlank(a)){
				resultList.add(a) ; 
			}
		}
		if(resultList.isEmpty() || resultList.size() <= start) {
			return "";
		}
		
		String result = "";
		for(; start<resultList.size() && start<end; start++){
			result += resultList.get(start)+"/";
		}
		
		if(StringUtils.isNotBlank(result)){
			result = StringUtils.substring(result, 0, result.length()-1);
		}
		
		return result;
	}
}
