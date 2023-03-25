package com.ib.certificate;

public interface ICertificateService {
	public Certificate findById(Long id);
	public Certificate accept(CertificateRequest req);
}
