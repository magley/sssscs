package com.ib.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.ib.user.exception.EmailTakenException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {
	@ExceptionHandler({EmailTakenException.class})
	public ResponseEntity<?> handleEmailTaken(final Exception e, final HttpServletRequest request) {
		return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
	}
}
