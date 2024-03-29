package com.ib.certificate.request;

import java.util.List;

import com.ib.certificate.Certificate;
import com.ib.certificate.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.user.User;
import com.ib.user.User.Role;
import com.ib.util.aspect.LogExecution;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class CertificateRequestService implements ICertificateRequestService {
	@Autowired
	private ICertificateRequestRepo certificateRequestRepo;

	@LogExecution
	@Override
	public CertificateRequest makeRequest(CertificateRequest request) {
		if (!request.isTypeValid()) {
			throw new CertificateParentMissingException();
		}

		if (!request.isCreatorAuthorized()) {
			throw new CreatorUnauthorizedException();
		}

		if (request.isChildOfRevokedParent()) {
			throw new ChildOfRevokedCertificateException();
		}

		if (request.parentIsEndCertificate()) {
			throw new ParentIsEndCertificateException();
		}

		if (request.isExpired()) {
			throw new BadExpirationDateException();
		}

		if (request.expiresAfterParent()) {
			throw new BadExpirationDateException();
		}

		return certificateRequestRepo.save(request);
	}

	@Override
	public CertificateRequest findByIdAndStatus(Long id, Status status) {
		return certificateRequestRepo.findByIdAndStatus(id, status)
				.orElseThrow(() -> new EntityNotFoundException(CertificateRequest.class, id));
	}

	@Override
	public boolean canAutoAccept(CertificateRequest request) {
		if (request.getType() != Type.ROOT) {
			if (request.getCreator().equals(request.getParent().getOwner())) {
				return true;
			}
		}

		if (request.getCreator().getRole() == Role.ADMIN) {
			return true;
		}

		return false;
	}

	@LogExecution
	@Override
	public List<CertificateRequest> findByCreator(User creator) {
		return certificateRequestRepo.findByCreator(creator);
	}
	
	@LogExecution
	@Override
	public List<CertificateRequest> findAll() {
		return certificateRequestRepo.findAll();
	}

	@Override
	public void reject(CertificateRequest req, String reason) {
		req.setStatus(Status.REJECTED);
		req.setRejectionReason(reason);
		certificateRequestRepo.save(req);
	}

	@Override
	public void accept(CertificateRequest req) {
		req.setStatus(Status.ACCEPTED);
		certificateRequestRepo.save(req);
	}

	@LogExecution
	@Override
	public List<CertificateRequest> findPendingRequestsIssuedTo(User user) {
		return certificateRequestRepo.findPendingRequestsIssuedTo(user.getId(), user.getRole() == Role.ADMIN);
	}

	@Override
	public List<CertificateRequest> findByParent(Certificate parent) {
		return certificateRequestRepo.findByParent(parent);
	}
}
