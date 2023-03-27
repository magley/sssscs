package com.ib.certificate;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.CertificateRequest.Status;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class CertificateService implements ICertificateService {
	@Autowired
	private ICertificateRepo certificateRepo;
	@Autowired
	private ICertificateRequestService certificateRequestService;
	
	@Override
	public Certificate findById(Long id) {
		return certificateRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(Certificate.class, id));
	}

	@Override
	public Certificate accept(CertificateRequest req) {
		certificateRequestService.setStatus(req, Status.ACCEPTED);
		
		Certificate c = new Certificate();
		c.setIssuer(req.getIssuer());
		c.setParent(req.getParent());
		c.setType(req.getType());
		c.setValidFrom(LocalDateTime.now());
		c.setValidTo(req.getValidTo());
		return certificateRepo.save(c);
	}
	
	@Override
	public List<Certificate> getAll() {
		return certificateRepo.findAll();
	}
}
