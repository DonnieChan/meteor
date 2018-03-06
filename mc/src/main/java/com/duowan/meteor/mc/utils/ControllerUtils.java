package com.duowan.meteor.mc.utils;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;

import com.duowan.meteor.model.view.AbstractBase;


public class ControllerUtils {

	static Logger logger = LoggerFactory.getLogger(ControllerUtils.class);
	
	public static String httpFlag = "https://mc.meteor.com";
	
	static {
		String dwenv = System.getenv("DWENV");
		if (!StringUtils.equals(dwenv, "prod")) {
			httpFlag = "http://mc.meteor.com";
		}
	}

	/**
	 * 写入 response
	 */
	public static void writeToResponse(Object object, HttpServletResponse response) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		String writedData = objectMapper.writeValueAsString(object);

		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();
		writer.write(writedData);
	}

	
	/** 提交form后的中转站，实现按自定义的target自由跳转 */
	public static String transferAfterSubmitForm(ModelMap model, AbstractBase abstractTask, String actionAfterSubmitForm, String targetAfterSubmitForm) {
		String messageAfterSubmitForm = "文件ID：" + abstractTask.getFileId() + "<br />"
				+ "文件名称：" + abstractTask.getFileName() + "<br />"
				+ "<br />"
				+ "提交成功！";
		
		model.put("messageAfterSubmitForm", messageAfterSubmitForm);
		model.put("actionAfterSubmitForm", actionAfterSubmitForm);
		model.put("targetAfterSubmitForm", targetAfterSubmitForm);

		return "commons/transferAfterSubmitForm";
	}
}
