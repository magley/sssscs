package com.ib.certificate.request;

import java.util.List;

import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.user.User;

public interface ICertificateRequestService {
	public CertificateRequest makeRequest(CertificateRequest request);

	public CertificateRequest findByIdAndStatus(Long id, Status status);

	public boolean canAutoAccept(CertificateRequest request);

	public List<CertificateRequest> findByCreator(User creator);
	
	public List<CertificateRequest> findAll();

	public List<CertificateRequest> findPendingRequestsIssuedTo(User user);

	public void accept(CertificateRequest req);

	public void reject(CertificateRequest req, String reason);
}
