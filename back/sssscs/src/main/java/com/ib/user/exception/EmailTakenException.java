package com.ib.user.exception;

import com.ib.common.EntityException;

public class EmailTakenException extends EntityException {
	private static final long serialVersionUID = 1L;

	public EmailTakenException() {
		super("Email already taken!");
	}
}
