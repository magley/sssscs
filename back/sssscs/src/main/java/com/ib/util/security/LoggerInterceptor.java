package com.ib.util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// https://www.baeldung.com/spring-mvc-handlerinterceptor

@Component
public class LoggerInterceptor implements HandlerInterceptor {
	private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);
	
	// Can't do Autowired here... why?
	private JwtTokenUtil jwtTokenUtil;
	
	public LoggerInterceptor() {
		this.jwtTokenUtil = new JwtTokenUtil();
	}

	private String getUserID(String authHeader) {
		String jwt = jwtTokenUtil.getJWTFromHeader(authHeader);
		if (jwt == null) {
			return null;
		}
		return jwtTokenUtil.getIdFromToken(jwt).toString();
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String auth = getUserID(request.getHeader("Authorization"));
		String userLog = "";
		if (auth == null) {
			userLog = "Anonymous User";
		} else {
			userLog = String.format("User with ID %s", auth);
		}
		
		log.info("Request  {} @ {} {}", userLog, request.getRequestURL().toString(), request.getMethod());	
	    return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	    // your code
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		String auth = getUserID(request.getHeader("Authorization"));
		String userLog = "";
		if (auth == null) {
			userLog = "Anonymous User";
		} else {
			userLog = String.format("User with ID %s", auth);
		}
		
		log.info("Response [{}] {} @ {} {}", HttpStatus.valueOf(response.getStatus()), userLog, request.getRequestURL().toString(), request.getMethod());	
	}
}