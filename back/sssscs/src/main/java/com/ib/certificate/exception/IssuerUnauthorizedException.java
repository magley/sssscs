package com.ib.certificate.exception;

import java.io.Serial;

import com.ib.util.exception.EntityException;

public class IssuerUnauthorizedException extends EntityException {
	@Serial
	private static final long serialVersionUID = 5135572596582665574L;

	public IssuerUnauthorizedException() {
		super("Issuer unauthorized for this type of certificate.");
	}

}
