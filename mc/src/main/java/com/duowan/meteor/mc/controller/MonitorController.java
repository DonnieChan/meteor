package com.duowan.meteor.mc.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.mc.common.controller.UserBaseController;

@Controller
public class MonitorController extends UserBaseController {

	@RequestMapping("/monitor/monitor.do")
	public void monitor(HttpServletResponse response) throws Exception {
		response.getWriter().print("200");
	}

}
