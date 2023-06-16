package com.ib.user.exception;

public class PasswordTooRecentException extends RuntimeException {
	private static final long serialVersionUID = 1364778517628276661L;

	public PasswordTooRecentException() {
		super("Password is too recent! Come up with a new one.");
	}
}
