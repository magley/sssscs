package com.ib.verification.exception;

public class InvalidCodeException extends RuntimeException {
	private static final long serialVersionUID = 5876057873812170754L;
	
	public InvalidCodeException() {
		super("Invalid code.");
	}
}
