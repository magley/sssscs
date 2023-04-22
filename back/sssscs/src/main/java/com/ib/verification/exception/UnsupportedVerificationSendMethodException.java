package com.ib.verification.exception;

import com.ib.util.exception.EntityException;
import com.ib.verification.dto.VerificationCodeSendRequestDto.Method;

public class UnsupportedVerificationSendMethodException extends EntityException {
	private static final long serialVersionUID = 7398270573619756175L;

	public UnsupportedVerificationSendMethodException(Method method) {
		super("Cannot send verification code. Unknown method " + method.toString());
	}
}
