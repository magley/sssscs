package com.ib.certificate;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.CertificateRequest.Status;
import com.ib.pki.KeyStoreUtil;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class CertificateService implements ICertificateService {
	@Autowired
	private ICertificateRepo certificateRepo;
	@Autowired
	private ICertificateRequestService certificateRequestService;
	@Autowired
	private KeyStoreUtil ksUtil;
	
	@Override
	public Certificate findById(Long id) {
		return certificateRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(Certificate.class, id));
	}

	@Override
	public Certificate accept(CertificateRequest req) {
		certificateRequestService.setStatus(req, Status.ACCEPTED);	
		Certificate c = new Certificate(req);
		return certificateRepo.save(c);
	}
	
	@Override
	public List<Certificate> getAll() {
		return certificateRepo.findAll();
	}

	@Override
	public boolean validate(Certificate cert) {
		if (isExpired(cert)) return false;
		if (isInvalidSignature(cert)) return false;
		
		if (isTrusted(cert)) {
			return true;
		} else {			
			return validate(cert.getParent());
		}
	}

	private boolean isTrusted(Certificate cert) {
		return  (cert.getType() == Type.ROOT);
	}
	
	private boolean isExpired(Certificate cert) {
		return (LocalDateTime.now().isBefore(cert.getValidFrom()) || LocalDateTime.now().isAfter(cert.getValidTo()));
	}
	
	private boolean isInvalidSignature(Certificate cert) {
		if (isTrusted(cert)) {
			return false;
		}
		try {
			PublicKey issuerPublicKey = ksUtil.getPublicKey(cert.getIssuer().getPublicKey());
			X509Certificate x509cert = (X509Certificate)ksUtil.getKs().getCertificate(cert.getSerialNumber());
			x509cert.verify(issuerPublicKey);
		} catch (SignatureException | InvalidKeyException exceptionsForWhenCertificateIsInvalid) {
			return true;
		} catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
}
