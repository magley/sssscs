package com.ib.config;

import com.ib.util.exception.EntityException;
import com.ib.util.exception.EntityNotFoundException;
import com.ib.util.BadValidation;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class ControllerAdvisor {
	@ExceptionHandler({EntityException.class})
	public ResponseEntity<?> handleEntityException(final EntityException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({EntityNotFoundException.class})
	public ResponseEntity<?> handleEntityNotFoundException(final EntityNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
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
