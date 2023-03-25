package com.ib.certificate;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.CertificateRequest.Status;
import com.ib.certificate.exception.BadExpirationDateException;
import com.ib.certificate.exception.InvalidCertificateTypeException;
import com.ib.certificate.exception.IssuerUnauthorizedException;
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
		if (!request.isTypeValid()) {
			throw new InvalidCertificateTypeException();
		}
		
		if (request.isExpired()) {
			throw new BadExpirationDateException();
		}
		
		if (!request.isIssuerAuthorized()) {
			throw new IssuerUnauthorizedException();
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
	
	@Override
	public Certificate findById(Long id) {
		return certificateRepo.findById(id).orElse(null);
	}

	@Override
	public CertificateRequest findReqByIdAndStatusEquals(Long id, Status status) {
		return certificateRequestRepo.findByIdAndStatusEquals(id, status).orElse(null);
	}

	@Override
	public Certificate accept(CertificateRequest req) {
		req.setStatus(Status.ACCEPTED);
		req = certificateRequestRepo.save(req);
		
		Certificate c = new Certificate();
		c.setIssuer(req.getIssuer());
		c.setParent(req.getParent());
		c.setType(req.getType());
		c.setValidFrom(LocalDateTime.now());
		c.setValidTo(req.getValidTo());
		return certificateRepo.save(c);
	}
}
