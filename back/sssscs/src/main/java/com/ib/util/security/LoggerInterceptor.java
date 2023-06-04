package com.ib.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// https://www.baeldung.com/spring-mvc-handlerinterceptor

public class LoggerInterceptor implements HandlerInterceptor {
	private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		log.info("{} {}", request.getRequestURL().toString(), request.getMethod());	
	    return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	    // your code
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		log.info("{}", HttpStatus.valueOf(response.getStatus()));	
	}
}