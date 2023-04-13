package com.ib.certificate;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.dto.CertificateSummaryItemDto;
import com.ib.certificate.request.CertificateRequest;
import com.ib.certificate.request.ICertificateRequestService;
import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.pki.KeyUtil;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class CertificateService implements ICertificateService {
	@Autowired
	private ICertificateRepo certificateRepo;
	@Autowired
	private ICertificateRequestService certificateRequestService;
	@Autowired
	private KeyUtil keyUtil;
	
	@Override
	public Certificate findById(Long id) {
		return certificateRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(Certificate.class, id));
	}

	@Override
	public Certificate accept(CertificateRequest req) {
		certificateRequestService.setStatus(req, Status.ACCEPTED);	
		Certificate c = new Certificate(req);
		c = certificateRepo.save(c);

		createX509Certificate(c, req);
		
		System.err.println("Checking validity...");
		System.err.println(validate(c));
		return c;
	}
	
	private X509Certificate createX509Certificate(Certificate c, CertificateRequest req) {
		Certificate parent = c.getParent();
		if (parent == null) {
			return createSelfSignedX509(c, req);
		} else {
			return createX509(c, req);
		}
	}
	
	private X509Certificate createSelfSignedX509(Certificate c, CertificateRequest req) {
		KeyPair keyPair = keyUtil.generateKeyPair();
		
		X509Certificate x509 = generateSelfSigned(keyPair, c.getValidFrom(), c.getValidTo());
		keyUtil.saveX509Certificate(c.getSerialNumber(), x509);
		keyUtil.savePrivateKey(c.getSerialNumber(), keyPair.getPrivate());

		return x509;
	}
	
	private X509Certificate createX509(Certificate c, CertificateRequest req) {
		Certificate parent = c.getParent();
		PrivateKey priv = keyUtil.getPrivateKey(parent.getSerialNumber());
		
		KeyPair keyPair = keyUtil.generateKeyPair();
	
		X509Certificate x509 = generate(c, req, keyPair.getPublic(), priv, c.getValidFrom(), c.getValidTo());
		keyUtil.saveX509Certificate(c.getSerialNumber(), x509);
		keyUtil.savePrivateKey(c.getSerialNumber(), keyPair.getPrivate());
		
		return x509;
	}
	
	private X509Certificate generate(Certificate c, CertificateRequest req, PublicKey subjectKey, PrivateKey issuerKey, LocalDateTime validFrom, LocalDateTime validTo) {
		Certificate parent = c.getParent();
		X509Certificate parent509 = keyUtil.getX509Certificate(parent.getSerialNumber());
		String issuerName = parent509.getSubjectX500Principal().getName(); // TODO: Formatting. Or some other way? Same for endpoint where we're getting certificates' summaries.
		
		try {	
			JcaContentSignerBuilder csbuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			csbuilder = csbuilder.setProvider("BC");
			ContentSigner signer = csbuilder.build(issuerKey);
			X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
				keyUtil.getX500Name(issuerName),
				BigInteger.valueOf(c.getId()),
				keyUtil.dateFrom(validFrom),
				keyUtil.dateFrom(validTo),
				keyUtil.getX500Name(req.getSubjectName()),
				subjectKey
			);
			
			X509CertificateHolder holder = builder.build(signer);
			JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
			conv = conv.setProvider("BC");
			return conv.getCertificate(holder);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private X509Certificate generateSelfSigned(KeyPair kp, LocalDateTime validFrom, LocalDateTime validTo) {
		try {	
			
			X500Name x500Name = new X500Name("CN=localhost");
			JcaContentSignerBuilder csbuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			csbuilder = csbuilder.setProvider("BC");
			ContentSigner signer = csbuilder.build(kp.getPrivate());
			X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
				x500Name,
				BigInteger.valueOf(Instant.now().getEpochSecond()),
				keyUtil.dateFrom(validFrom),
				keyUtil.dateFrom(validTo),
				x500Name,
				kp.getPublic()
			);
			
			X509CertificateHolder holder = builder.build(signer);
			JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
			conv = conv.setProvider("BC");
			return conv.getCertificate(holder);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
		return (cert.getType() == Type.ROOT);
	}
	
	private boolean isExpired(Certificate cert) {
		if (cert.getValidFrom().isAfter(LocalDateTime.now())) {
			return true;
		}
		if (LocalDateTime.now().isAfter(cert.getValidTo())) {
			return true;
		}
		return false;
		//return (LocalDateTime.now().isBefore(cert.getValidFrom()) || LocalDateTime.now().isAfter(cert.getValidTo()));
	}
	
	private boolean isInvalidSignature(Certificate cert) {
		if (isTrusted(cert)) {
			return false;
		}
		
		X509Certificate self509 = keyUtil.getX509Certificate(cert.getSerialNumber());
		X509Certificate parent509 = keyUtil.getX509Certificate(cert.getParent().getSerialNumber());
		if (parent509 == null) {
			throw new RuntimeException("This should not happen.");
		}
		
		try {
			self509.verify(parent509.getPublicKey());
		} catch (SignatureException | InvalidKeyException e) {
			return true;
		} catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}

	@Override
	public List<CertificateSummaryItemDto> getAllSummary() {
		List<Certificate> certs = getAll();
		List<CertificateSummaryItemDto> result = new ArrayList<>();
		for (Certificate cert : certs) {
			CertificateSummaryItemDto item = new CertificateSummaryItemDto();
			X509Certificate cert509 = keyUtil.getX509Certificate(cert.getSerialNumber());
			
			item.setId(cert.getId());
			item.setType(cert.getType());
			item.setValidFrom(cert.getValidFrom());
			item.setSubjectName(cert509.getSubjectX500Principal().getName()); // TODO: Extract CN=something -> something.
			result.add(item);
		}
		
		return result;
	}

	@Override
	public boolean isValid(Certificate cert) {
		return validate(cert);
	}
}
