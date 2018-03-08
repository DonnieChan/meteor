package com.duowan.meteor.mc.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.duowan.meteor.mc.common.controller.UserBaseController;


@Controller
public class LoginController extends UserBaseController {

	private Log log = LogFactory.getLog(this.getClass());

	public static final String ADMIN_IN_COOKIE_KEY = "meteor.mc.cookie";

	public static final String ADMIN_IN_PASSPORT_KEY = "meteor.mc.passport";

	/** Cookie 值分割符 */
	public static final String COOKIE_SPLIT_FLAG = "`==`";

	/** 水印字符码 */
	public static final String SIGN = "123456789abcdefg";


	/**
	 * 登录 ,写session、Cookie
	 * 
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/login.do")
	public String login(String passport, String password, HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
		log.info("run login");
		if (!validateLogin(passport, password)) {
			return "login";
		}
		log.info("passport = " + passport);

		setLoginCookie(passport);

		return "redirect:/home.do";
	}

	private boolean validateLogin(String passport, String password) {
		log.debug("run validateLogin");
		if (!isPost()) {
			return false;
		}
		if (StringUtils.isBlank(passport)) {
			model.addAttribute("errorMessage", "通行证不能为空!");
			return false;
		}
		if (StringUtils.isBlank(password)) {
			model.addAttribute("errorMessage", "密码不能为空!");
			return false;
		}

		return true;
	}

	/**
	 * 登出
	 * 
	 * @return
	 */
	@RequestMapping("/logout.do")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.invalidate();
		return "redirect:/login.do";
	}

	
	public void setLoginCookie(String passport) {
		String time = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssS");
		String userAgent = getHttpServletRequest().getHeader("user-agent");
		String sign = DigestUtils.md5Hex(passport + time + userAgent + SIGN);
		String adminInCookieValue = passport + COOKIE_SPLIT_FLAG + time + COOKIE_SPLIT_FLAG + sign;
		Cookie adminInfo = new Cookie(ADMIN_IN_COOKIE_KEY, adminInCookieValue);
		adminInfo.setMaxAge(24 * 60 * 60);
		HttpServletResponse response = getHttpServletResponse();
		response.addCookie(adminInfo);
	}
}
