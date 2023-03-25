package com.ib.certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.user.User.Role;

@Service
public class CertificateService implements ICertificateService {
	@Autowired
	private ICertificateRepo certificateRepo;
	@Autowired
	private ICertificateRequestRepo certificateRequestRepo;
	
	// TODO: Split CertificateService into CertificateRequestService.

	@Override
	public CertificateRequest makeRequest(CertificateRequest request) {
		// TODO: Create custom exceptions.
		
		if (!request.isTypeValid()) {
			throw new RuntimeException("Invalid certifice type.");
		}
		
		if (request.isExpired()) {
			throw new RuntimeException("Bad expiration date");
		}
		
		if (!request.isIssuerAuthorized()) {
			throw new RuntimeException("Regular user cannot create root certificate");
		}
		
		if (canAutoAccept(request)) {
			// Call service function to accept request and create certificate.
		}
		
		return certificateRequestRepo.save(request);
	}
	
	private boolean canAutoAccept(CertificateRequest request) {
		if (request.getType() != Type.ROOT) {
			if (request.getIssuer().equals(request.getParent().getIssuer())) {
				return true;
			}
		}
		
		if (request.getIssuer().getRole() == Role.ADMIN) {
			return true;
		}
		
		return false;
	}
}
