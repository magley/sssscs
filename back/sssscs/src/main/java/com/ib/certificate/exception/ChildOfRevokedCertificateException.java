package com.ib.certificate.exception;

import com.ib.util.exception.EntityException;

import java.io.Serial;

public class ChildOfRevokedCertificateException extends EntityException {

    @Serial
	private static final long serialVersionUID = -4918573057166483970L;

	public ChildOfRevokedCertificateException() {
        super("Certificate is a child of a revoked certificate.");
    }
}
