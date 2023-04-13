package com.ib.certificate.request;

import java.util.List;

import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.user.User;

public interface ICertificateRequestService {
	public CertificateRequest makeRequest(CertificateRequest request);
	public CertificateRequest findByIdAndStatus(Long id, Status status);
	public CertificateRequest setStatus(CertificateRequest reqeust, Status status);
	public boolean canAutoAccept(CertificateRequest request);
	public List<CertificateRequest> findByCreator(User creator);
	public List<CertificateRequest> findByIssuee(User issuee);
	public void reject(CertificateRequest req, String reason);
}