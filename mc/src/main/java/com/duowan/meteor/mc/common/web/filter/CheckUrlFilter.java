package com.duowan.meteor.mc.common.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;

import com.duowan.meteor.mc.common.controller.UserBaseController;

public class CheckUrlFilter implements Filter {

	private static Logger logger = (Logger) LoggerFactory.getLogger(CheckUrlFilter.class);

	public static final String LOGIN_PAGE = "/login.jsp";
	public static final String ERROR_PAGE = "/error.jsp";
	public static final String MULTIPART = "multipart/";

	/** 不需要验证的请求 */
	private static List<String> skipPaths = new ArrayList<String>();
	private static List<String> ignoreTypes = new ArrayList<String>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		skipPaths = splitToList(filterConfig.getInitParameter("skipPaths"));
		ignoreTypes = splitToList(filterConfig.getInitParameter("ignoreTypes"));
	}

	@Override
	public void doFilter(ServletRequest sRequest, ServletResponse sResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) sRequest;
		HttpServletResponse response = (HttpServletResponse) sResponse;

		if (isMultipartContent(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		/** path */
		String path = request.getServletPath();
		path = path != null ? path : request.getRequestURI();
		if (StringUtils.isBlank(path)) {
			response.sendRedirect(LOGIN_PAGE);
			return;
		}

		if (isSkipedPath(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		/** passport */
		String passport = UserBaseController.getLoginPassport(request);

		if (StringUtils.isBlank(passport)) {
			response.sendRedirect(LOGIN_PAGE);
			return;
		}
		logger.info("passport: " + passport + ", path: " + path);

		filterChain.doFilter(sRequest, sResponse);
	}

	@Override
	public void destroy() {

	}

	/**
	 * Part of HTTP content type header.
	 */
	public static boolean isMultipartContent(HttpServletRequest request) {
		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}
		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}
		if (contentType.toLowerCase().startsWith(MULTIPART)) {
			return true;
		}
		return false;
	}

	/** splitToList */
	public static List<String> splitToList(String content) {
		List<String> list = new ArrayList<String>();
		if (content != null) {
			String[] items = content.split(",");
			String item;
			for (int i = 0; i < items.length; i++) {
				item = StringUtils.trimToEmpty(items[i]);
				if (StringUtils.isNotBlank(item)) {
					list.add(item);
				}
			}
		}
		return list;
	}

	/** isSkipedPath */
	private static boolean isSkipedPath(String path) {
		if (isMatchedUrlPath(path, skipPaths)) {
			return true;
		}

		path = optimizeUrlPath(path);
		for (String endStr : ignoreTypes) {
			if (StringUtils.endsWith(path, endStr)) {
				return true;
			}
		}

		return false;
	}

	/** isMatchedUrlPath */
	public static boolean isMatchedUrlPath(String path, List<String> pathList) {
		if (StringUtils.isBlank(path) || pathList == null) {
			return false;
		}

		path = optimizeUrlPath(path);
		AntPathMatcher matcher = new AntPathMatcher();
		for (String skip : pathList) {
			if (matcher.match(skip, path)) {
				return true;
			}
		}

		return false;
	}

	/** optimizeUrlPath */
	public static String optimizeUrlPath(String path) {
		path = StringUtils.trimToEmpty(path);
		path = StringUtils.replace(path, "\\", "/");

		return path;
	}
}
