package com.ib.certificate;

public interface ICertificateService {
	public CertificateRequest makeRequest(CertificateRequest request);
	public Certificate findById(Long id);
	public CertificateRequest findReqById(Long id);
	public Certificate save(CertificateRequest req);
}
