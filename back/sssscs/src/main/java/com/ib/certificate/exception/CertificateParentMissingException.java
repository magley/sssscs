package com.ib.certificate.exception;

import java.io.Serial;

import com.ib.util.exception.EntityException;

public class CertificateParentMissingException extends EntityException {
	@Serial
	private static final long serialVersionUID = 3544584791580877663L;

	public CertificateParentMissingException() {
		super("Non-root certificate must have a parent.");
	}

}
