package com.ib.user.exception;

import java.io.Serial;

import com.ib.util.exception.EntityException;

public class EmailTakenException extends EntityException {
	@Serial
	private static final long serialVersionUID = 1L;

	public EmailTakenException() {
		super("Email already taken!");
	}
}
