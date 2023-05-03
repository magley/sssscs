package com.ib.certificate.exception;

import java.io.Serial;

import com.ib.util.exception.EntityException;

public class RevocationUnauthorizedException extends EntityException {

	@Serial
	private static final long serialVersionUID = 3069692032475710801L;

	public RevocationUnauthorizedException() {
		super("User cannot revoke this certificate.");
	}
}
