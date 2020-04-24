package com.appleyk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageIndexController {
	//设置首页
	@RequestMapping("/")
	public String index() {
		return "index";
	}
}
