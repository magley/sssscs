package com.ib.certificate;

import com.ib.certificate.CertificateRequest.Status;

public interface ICertificateService {
	public CertificateRequest makeRequest(CertificateRequest request);
	public Certificate findById(Long id);
	public CertificateRequest findReqByIdAndStatusEquals(Long id, Status status);
	public Certificate accept(CertificateRequest req);
}
