package com.a.eye.bi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author Taejin Koo
 */

@Controller
public class ApplicationController {

	@RequestMapping(value = "/getApplicationHostInfo", method = RequestMethod.GET)
	@ResponseBody
	public void getApplicationHostInfo(@RequestParam(value = "offset", required = false, defaultValue = "1") int offset,
			@RequestParam(value = "limit", required = false, defaultValue = "100") int limit) throws Exception {
	}

}