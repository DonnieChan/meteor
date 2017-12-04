package com.duowan.meteor.util;

import java.io.IOException;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;

/**
 * json相关操作工具类
 * 
 * @author chenwu
 */
public class JsonUtils {

	private final static ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		objectMapper.configure(JsonParser.Feature.INTERN_FIELD_NAMES, true);
		objectMapper.configure(JsonParser.Feature.CANONICALIZE_FIELD_NAMES, true);
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.disable(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS);
		objectMapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
	}

	private JsonUtils() {
	}

	/**
	 * 将对象格式化成json字符串
	 * 
	 * @param obj
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String encode(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
		return objectMapper.writeValueAsString(obj);
	}

	/**
	 * 将对象格式化成json字符串
	 * 
	 * @param obj
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String encodeFilter(Object obj, String filterName, Set<String> filterProps) throws JsonGenerationException, JsonMappingException, IOException {
		FilterProvider filterProvider = new SimpleFilterProvider().addFilter(filterName, SimpleBeanPropertyFilter.serializeAllExcept(filterProps));
		return objectMapper.writer(filterProvider).writeValueAsString(obj);
	}

	/**
	 * 将json string反序列化成对象
	 * 
	 * @param json
	 * @param valueType
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static <T> T decode(String json, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		return objectMapper.readValue(json, valueType);
	}

	/**
	 * 将json array反序列化为对象
	 * 
	 * @param json
	 * @param jsonTypeReference
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T decode(String json, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException, IOException {
		return (T) objectMapper.readValue(json, typeReference);
	}

}
