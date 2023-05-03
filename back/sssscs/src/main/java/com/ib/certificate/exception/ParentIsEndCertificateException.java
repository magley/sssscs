package com.ib.certificate.exception;

import com.ib.util.exception.EntityException;

import java.io.Serial;

public class ParentIsEndCertificateException extends EntityException {

	@Serial
	private static final long serialVersionUID = -7605125288606396264L;

	public ParentIsEndCertificateException() {
		super("Parent of certificate is an END certificate.");
	}

}
