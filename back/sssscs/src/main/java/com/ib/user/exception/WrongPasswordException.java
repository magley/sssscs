package com.ib.user.exception;

public class WrongPasswordException extends RuntimeException {
	private static final long serialVersionUID = 1364178517628276661L;

	public WrongPasswordException() {
		super("Password is incorrect!");
	}
}
