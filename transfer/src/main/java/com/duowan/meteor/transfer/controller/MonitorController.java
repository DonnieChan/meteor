package com.duowan.meteor.transfer.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MonitorController {

	@RequestMapping("/monitor/monitor.do")
	public void monitor(HttpServletResponse response) throws Exception {
		response.getWriter().print("200");
	}

}
