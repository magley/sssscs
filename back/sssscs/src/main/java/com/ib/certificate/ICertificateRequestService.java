package com.ib.certificate;

import com.ib.certificate.CertificateRequest.Status;

public interface ICertificateRequestService {
	public CertificateRequest makeRequest(CertificateRequest request);
	public CertificateRequest findByIdAndStatusEquals(Long id, Status status);
	public CertificateRequest setStatus(CertificateRequest reqeust, Status status);
	public boolean canAutoAccept(CertificateRequest request);
}
