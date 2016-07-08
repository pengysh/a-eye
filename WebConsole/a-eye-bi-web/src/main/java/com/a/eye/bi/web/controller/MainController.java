package com.a.eye.bi.web.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.a.eye.bi.web.service.CommonService;
import com.a.eye.bi.web.view.Application;
import com.a.eye.bi.web.view.ApplicationGroup;
import com.a.eye.bi.web.view.ServerTime;

/**
 * @author emeroad
 * @author netspider
 */
@Controller
public class MainController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/applications", method = RequestMethod.GET)
	@ResponseBody
	public ApplicationGroup getApplicationGroup() {
		List<Application> applicationList = commonService.selectAllApplicationNames();
		logger.debug("/applications {}", applicationList);

		return new ApplicationGroup(applicationList);
	}

	@RequestMapping(value = "/serverTime", method = RequestMethod.GET)
	@ResponseBody
	public ServerTime getServerTime() {
		return new ServerTime();
	}
}
