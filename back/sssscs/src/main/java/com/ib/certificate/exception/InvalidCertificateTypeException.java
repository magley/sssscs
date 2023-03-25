package com.ib.certificate.exception;

import com.ib.util.exception.EntityException;

public class InvalidCertificateTypeException extends EntityException {
	private static final long serialVersionUID = 3544584791580877663L;

	public InvalidCertificateTypeException() {
		super("Certificate type is not valid.");
	}

}
