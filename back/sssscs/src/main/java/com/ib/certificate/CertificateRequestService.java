package com.ib.certificate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.CertificateRequest.Status;
import com.ib.certificate.exception.BadExpirationDateException;
import com.ib.certificate.exception.InvalidCertificateTypeException;
import com.ib.certificate.exception.IssuerUnauthorizedException;
import com.ib.user.User;
import com.ib.user.User.Role;
import com.ib.util.exception.EntityNotFoundException;

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
		return certificateRequestRepo.findByIdAndStatusEquals(id, status).orElseThrow(() -> new EntityNotFoundException(CertificateRequest.class, id));
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

	@Override
	public List<CertificateRequest> findByIssuer(User issuer) {
		return certificateRequestRepo.findByIssuer(issuer);
	}

	@Override
	public List<CertificateRequest> findByIssuee(User issuee) {
		return certificateRequestRepo.findByIssuee(issuee.getId(), issuee.getRole() == Role.ADMIN);
	}

	@Override
	public void reject(CertificateRequest req, String reason) {
		setStatus(req, Status.ACCEPTED);
		req.setRejectionReason(reason);
		certificateRequestRepo.save(req);
	}
}