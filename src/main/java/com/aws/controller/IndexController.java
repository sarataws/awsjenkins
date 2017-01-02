package com.aws.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

	@Autowired
	private S3Helper s3Helper;

	Logger logger = LoggerFactory.getLogger(IndexController.class);

	@RequestMapping(value = { "/index" }, method = RequestMethod.GET)
	@ResponseBody
	public String getIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String displayPage = s3Helper.getIndexPage();
		if (!StringUtils.isEmpty(displayPage)) {
			logger.info("index page is loaded in this server");
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(displayPage);
		} else {
			logger.error("List Build Index page is empty");
		}
		return null;

	}

	@RequestMapping(value = { "/user/index" }, method = RequestMethod.GET)
	@ResponseBody
	public String getUserIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String displayPage = s3Helper.getUserIndexPage();
		if (!StringUtils.isEmpty(displayPage)) {
			logger.info("user index page is loaded in this server");
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(displayPage);
		} else {
			logger.error("List Build Index page is empty");
		}
		return null;

	}

	@RequestMapping(value = { "/admin/index" }, method = RequestMethod.GET)
	@ResponseBody
	public String getAdminIndex(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String displayPage = s3Helper.getAdminIndexPage();
		if (!StringUtils.isEmpty(displayPage)) {
			logger.info("admin index page is loaded in this server");
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(displayPage);
		} else {
			logger.error("List Build Index page is empty");
		}
		return null;

	}

	@RequestMapping(value = { "/healthCheck" }, method = RequestMethod.GET)
	@ResponseBody
	public String getHealthCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.info("HealthCheck is hit by elb");
		return "success";

	}

}
