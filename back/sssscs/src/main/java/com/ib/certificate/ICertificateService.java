package com.ib.certificate;

import java.util.List;

public interface ICertificateService {
	public Certificate findById(Long id);
	public Certificate accept(CertificateRequest req);
	public List<Certificate> getAll();
}
