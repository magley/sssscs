package com.ib.user.exception;

import com.ib.util.exception.EntityException;

import java.io.Serial;

public class EmailTakenException extends EntityException {
	@Serial
	private static final long serialVersionUID = 1L;

	public EmailTakenException() {
		super("Email already taken!");
	}
}
