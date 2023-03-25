package com.ib.certificate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.CertificateRequest.Status;
import com.ib.certificate.exception.BadExpirationDateException;
import com.ib.certificate.exception.InvalidCertificateTypeException;
import com.ib.certificate.exception.IssuerUnauthorizedException;
import com.ib.user.User.Role;

@Service
public class CertificateRequestService implements ICertificateRequestService {
	@Autowired
	private ICertificateRequestRepo certificateRequestRepo;

	@Override
	public CertificateRequest makeRequest(CertificateRequest request) {
		if (!request.isTypeValid()) {
			throw new InvalidCertificateTypeException();
		}
		
		if (request.isExpired()) {
			throw new BadExpirationDateException();
		}
		
		if (!request.isIssuerAuthorized()) {
			throw new IssuerUnauthorizedException();
		}
		
		return certificateRequestRepo.save(request);
	}
	
	@Override
	public CertificateRequest findByIdAndStatusEquals(Long id, Status status) {
		return certificateRequestRepo.findByIdAndStatusEquals(id, status).orElse(null);
	}

	@Override
	public CertificateRequest setStatus(CertificateRequest req, Status status) {
		req.setStatus(status);
		return certificateRequestRepo.save(req);
	}
	
	@Override
	public boolean canAutoAccept(CertificateRequest request) {
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
