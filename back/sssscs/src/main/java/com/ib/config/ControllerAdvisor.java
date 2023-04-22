package com.ib.config;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.ib.certificate.exception.CreatorUnauthorizedException;
import com.ib.util.exception.EntityException;
import com.ib.util.exception.EntityNotFoundException;
import com.ib.util.validation.BadValidation;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerAdvisor {
	@ExceptionHandler({ResponseStatusException.class})
	public ResponseEntity<?> handleResponseStatusException(final ResponseStatusException e) {
		return new ResponseEntity<>(e.getMessage(), e.getStatusCode());
	}
	
	@ExceptionHandler({EntityException.class})
	public ResponseEntity<?> handleEntityException(final EntityException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EntityNotFoundException.class})
	public ResponseEntity<?> handleEntityNotFoundException(final EntityNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler({CreatorUnauthorizedException.class})
	public ResponseEntity<?> handleCreatorUnauhotirzedException(final CreatorUnauthorizedException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler({ MethodArgumentNotValidException.class })
	public ResponseEntity<BadValidation> handleConstraintViolationException(MethodArgumentNotValidException e, HttpServletRequest req) {
		List<FieldError> errors = e.getFieldErrors();
		StringBuilder sb = new StringBuilder("Request finished with validation errors:\n");
		for (FieldError fe : errors) {
			sb.append("Field ");
			sb.append(fe.getField());
			sb.append(" ");
			sb.append(fe.getDefaultMessage());
			sb.append("!\n");
		}
		BadValidation badValidation = new BadValidation(System.currentTimeMillis(), sb.toString(), "Bad Request",
				req.getRequestURI());
		System.err.println(badValidation.getMessage());
		return new ResponseEntity<>(badValidation, HttpStatus.BAD_REQUEST);
	}
}
