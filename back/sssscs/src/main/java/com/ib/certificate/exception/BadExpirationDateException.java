package com.ib.certificate.exception;

import java.io.Serial;

import com.ib.util.exception.EntityException;

public class BadExpirationDateException extends EntityException {
	@Serial
	private static final long serialVersionUID = -7029294265326536630L;

	public BadExpirationDateException() {
		super("Bad expiration date (must not be in the past).");
	}

}
