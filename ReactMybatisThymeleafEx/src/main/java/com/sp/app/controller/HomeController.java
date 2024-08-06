package com.sp.app.controller;

import java.time.LocalDateTime;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	@GetMapping("/")
	public ModelAndView main(ModelAndView mav) {
		mav.setViewName("home");
		mav.addObject("localDateTime", LocalDateTime.now());
		
		return mav;
	}
}
