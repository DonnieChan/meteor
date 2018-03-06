package com.duowan.meteor.server.udf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.server.util.ValidUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 * 描述：通用字符串验证，过滤掉非法的字符
 * 
 * 函数原型：validate_string
 * 
 * 参数说明: str：验证字符串；defaultStr：默认字符串；validType：正则类型
    NOT_CHAR_NUM_("[^a-zA-Z_0-9/]"), 
    NOT_BLANK("[^\\S]"),
    //字符串中间允许空格和点号
    NOT_CHAR_NUM_SP_DOT_("^\\s|\\s$|[^a-zA-Z_0-9/\\s\\.\\(\\)\\-]"),
    //[\u4E00-\u9FA5]汉字﹐[\uFE30-\uFFA0]全角字符
    NOT_CHAR_NUM_SP_DOT_CN_("^\\s|\\s$|[^a-zA-Z_0-9/\\s\\.\u4E00-\u9FA5\uFE30-\uFFA0]");
 * 
 * 返回值: String
 * 
 * 使用例子：
	select a,b from table where validate_string(country) = true;
 * 
 * 
 * 
 */
public class UDFValidateString {
	
	private static Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(5000000).expireAfterAccess(10, TimeUnit.MINUTES).concurrencyLevel(20).build();
	private static Map<String, Pattern> patternMap = new HashMap<String, Pattern>();
	
	private static final int MAX_LENGTH = 50;
	
	static {
		for(ValidStringType a : ValidStringType.values()){
			Pattern p = Pattern.compile(a.getPattern());
			patternMap.put(a.name(), p);
		}
	}

	public static boolean evaluate(final String str) {
		return ValidUtil.isValidString(str);
	}
	
	public static String evaluate(final String str ,final String defaultStr) {
		return evaluate(str, defaultStr, ValidStringType.NOT_BLANK.name()) ;
	}
	
	public static String evaluate(final String str ,  final String defaultStr,final String validType) {
		if(StringUtils.isBlank(defaultStr) || StringUtils.length(defaultStr) > MAX_LENGTH ){
			throw new IllegalArgumentException("非法默认值! defaultStr=" + defaultStr);
		}
		
		if(StringUtils.isBlank(str)){
			return defaultStr ;
		}
		
		String key = str + "\001" + defaultStr + "\001" + validType;
		String value = cache.getIfPresent(key);
		if (value != null) {
			return value;
		}
		
		Pattern p = patternMap.get(validType); 
		Matcher m = p.matcher(str);
		value = m.find() ? defaultStr : str;
		cache.put(key, value);
		return  value;
	}
	
	 public enum ValidStringType {  
		 	NOT_URL_ENCODE("(.+%-)"), 
	        NOT_CHAR_NUM_("[^a-zA-Z_\\-0-9/]"), 
	        NOT_BLANK("^\\s|\\s$"),
	        //字符串中间允许空格和点号
	        NOT_CHAR_NUM_SP_DOT_("^\\s|\\s$|[^a-zA-Z_\\-0-9/\\s\\.\\=\\(\\)\\-]"),
	        NOT_CHAR_NUM_SP_DOT_OR_NULL_("^\\s|\\s$|[^a-zA-Z_\\-0-9/\\s\\.]|^(?i)null"),
	        //[\u4E00-\u9FA5]汉字﹐[\uFE30-\uFFA0]全角字符
	        NOT_CHAR_NUM_SP_DOT_CN_("^\\s|\\s$|[^a-zA-Z_\\[\\]\\-0-9/\\s\\.\u4E00-\u9FA5\uFE30-\uFFA0]");
	        // 成员变量   
	        private  String pattern;  
	        // 构造方法   
	        private ValidStringType( String pattern) {
	        	this.pattern = pattern;
	        }
	        
			public String getPattern() {
				return pattern;
			}

			public void setPattern(String pattern) {
				this.pattern = pattern;
			}

			public static ValidStringType getValidStringType(String s){
				for(ValidStringType a : ValidStringType.values()){
					if(StringUtils.endsWithIgnoreCase(s, a.name())){
						return a ; 
					}
				}
				return NOT_BLANK ; 
			}
	}
	
	public static void main(String[] args) {
		String str = " abc";
		String validType = "NOT_BLANK";
		Pattern p = patternMap.get(validType); 
		Matcher m = p.matcher(str);
		String value = m.find() ? "false" : "true";
		System.out.println(value);
	}
}
