package com.ib.certificate;

import java.security.KeyStoreException;
import java.util.List;

public interface ICertificateService {
	public Certificate findById(Long id);
	public Certificate accept(CertificateRequest req);
	public List<Certificate> getAll();
	
	public boolean validate(Certificate cert);
}
