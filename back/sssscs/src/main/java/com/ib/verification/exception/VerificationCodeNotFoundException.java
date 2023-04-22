package com.ib.verification.exception;

import com.ib.user.User;
import com.ib.util.exception.EntityException;

public class VerificationCodeNotFoundException extends EntityException {
	private static final long serialVersionUID = 4852583495785086556L;

	public VerificationCodeNotFoundException(User user) {
		super("No code for user " + user.getEmail() + " found.");
	}
}
