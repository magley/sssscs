package com.ib.util.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ib.user.User;
import com.ib.util.security.IAuthenticationFacade;
import com.ib.util.security.LoggerInterceptor;

@Aspect
@Component
public class LogAspect {
	private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);
	
	@Autowired
	private IAuthenticationFacade auth;
	
	private String getUserLog() {
		User user = null;
		String userLog = "Anonymous user";
		try {
			user = auth.getUser();
		} catch (RuntimeException e) {
			// Nothing.
		}
		if (user != null) {
			userLog = String.format("User with ID %d", user.getId());
		}
		return userLog;
	}
	
	@Around("@annotation(LogExecution)")
	public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
		String userLog = getUserLog();
		
		log.info("{} started action {}", userLog, joinPoint.getSignature().getName());
	    Object proceed = joinPoint.proceed();
	    log.info("{} ended   action {}", userLog, joinPoint.getSignature().getName());
	    return proceed;
	}
}
