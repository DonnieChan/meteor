package com.duowan.meteor.mc.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.mc.common.controller.UserBaseController;
import com.duowan.meteor.mc.utils.ControllerUtils;


@Controller
public class IndexController extends UserBaseController {

	@RequestMapping("/home.do")
	public String home(ModelMap model) {
		String passport = getLoginPassport();
		if (StringUtils.isBlank(passport)) {
			return "redirect:" + ControllerUtils.httpFlag + "/login.do";
		}
		model.put("passport", passport);
		model.put("projectName", "流星实时数据开发平台");
		return "home";
	}

}
