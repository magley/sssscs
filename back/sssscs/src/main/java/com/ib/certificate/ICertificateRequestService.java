package com.ib.certificate;

import java.util.List;

import com.ib.certificate.CertificateRequest.Status;
import com.ib.user.User;

public interface ICertificateRequestService {
	public CertificateRequest makeRequest(CertificateRequest request);
	public CertificateRequest findByIdAndStatusEquals(Long id, Status status);
	public CertificateRequest setStatus(CertificateRequest reqeust, Status status);
	public boolean canAutoAccept(CertificateRequest request);
	public List<CertificateRequest> findByIssuer(User issuer);
}
