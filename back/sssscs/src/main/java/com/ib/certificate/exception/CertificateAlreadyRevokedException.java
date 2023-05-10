package com.ib.certificate.exception;

import java.io.Serial;

import com.ib.util.exception.EntityException;

public class CertificateAlreadyRevokedException extends EntityException {

	@Serial
	private static final long serialVersionUID = -8853078816729674485L;

	public CertificateAlreadyRevokedException() {
		super("Cannot revoke a certificate that has already been revoked.");
	}

}
