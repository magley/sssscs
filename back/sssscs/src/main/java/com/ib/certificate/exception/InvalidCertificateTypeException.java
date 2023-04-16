package com.ib.certificate.exception;

import java.io.Serial;

import com.ib.certificate.Certificate;
import com.ib.util.exception.EntityException;

public class InvalidCertificateTypeException extends EntityException {
	@Serial
	private static final long serialVersionUID = -7449952333432832350L;

	public InvalidCertificateTypeException(Certificate cert) {
		super("Certificate type is not valid for this operation (" + cert.getType().toString() + ")");
	}

}
