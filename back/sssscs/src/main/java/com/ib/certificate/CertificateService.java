package com.ib.certificate;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.certificate.Certificate.Type;
import com.ib.certificate.CertificateRequest.Status;
import com.ib.pki.manual.KeyUtil;
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
		return c;
	}
	
	private X509Certificate createX509Certificate(Certificate c, CertificateRequest req) {
		Certificate parent = c.getParent();
		if (parent == null ) {
			return createSelfSignedX509(c, req);
		} else {
			return createX509(c, req);
		}

	}
	
	private X509Certificate createSelfSignedX509(Certificate c, CertificateRequest req) {
		KeyPair keyPair = keyUtil.generateKeyPair();
		
		//X509Certificate x509 = generateSelfSigned(keyPair, c.getValidFrom(), c.getValidTo());
		X509Certificate x509 = null;
		try {
			x509 = generateSelfSigned(keyPair, "SHA256WithRSAEncryption", "god", 365);
		} catch (OperatorCreationException | CertificateException | CertIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keyUtil.saveX509Certificate(c.getSerialNumber(), x509);
		keyUtil.savePrivateKey(c.getSerialNumber(), keyPair.getPrivate());
		
		System.err.println(x509.toString());
		
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
		try {	
			JcaContentSignerBuilder csbuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			csbuilder = csbuilder.setProvider("BC");
			ContentSigner signer = csbuilder.build(issuerKey);
			X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
				keyUtil.getX500Name("AA"),
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
	
	public X509Certificate generateSelfSigned(KeyPair keyPair,
            String hashAlgorithm,
            String cn,
            int days)
		throws OperatorCreationException, CertificateException, CertIOException
	{
		final Instant now = Instant.now();
		final Date notBefore = Date.from(now);
		final Date notAfter = Date.from(now.plus(Duration.ofDays(days)));
		
		final ContentSigner contentSigner = new JcaContentSignerBuilder(hashAlgorithm).build(keyPair.getPrivate());
		final X500Name x500Name = new X500Name("CN=" + cn);
		final X509v3CertificateBuilder certificateBuilder =
		new JcaX509v3CertificateBuilder(x500Name,
		BigInteger.valueOf(now.toEpochMilli()),
		notBefore,
		notAfter,
		x500Name,
		keyPair.getPublic())
		.addExtension(Extension.subjectKeyIdentifier, false, KeyUtil.createSubjectKeyId(keyPair.getPublic()))
		.addExtension(Extension.authorityKeyIdentifier, false, KeyUtil.createAuthorityKeyId(keyPair.getPublic()))
		.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
		
		return new JcaX509CertificateConverter()
		.setProvider("BC").getCertificate(certificateBuilder.build(contentSigner));
	}
	
//	private X509Certificate generateSelfSigned(KeyPair kp, LocalDateTime validFrom, LocalDateTime validTo) {
//		try {	
//			
//			X500Name x500Name = new X500Name("CN=god");
//			JcaContentSignerBuilder csbuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
//			csbuilder = csbuilder.setProvider("BC");
//			ContentSigner signer = csbuilder.build(kp.getPrivate());
//			X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
//				x500Name,
//				BigInteger.valueOf(Instant.now().getEpochSecond()),
//				keyUtil.dateFrom(validFrom),
//				keyUtil.dateFrom(validTo),
//				x500Name,
//				kp.getPublic()
//			).addExtension(Extension.subjectKeyIdentifier, false, KeyUtil.createSubjectKeyId(kp.getPublic()))       
//			 .addExtension(Extension.authorityKeyIdentifier, false, KeyUtil.createAuthorityKeyId(kp.getPublic()))
//			 .addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
//			
//			X509CertificateHolder holder = builder.build(signer);
//			JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
//			conv = conv.setProvider("BC");
//			return conv.getCertificate(holder);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
		
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
		return false;
		
//		if (isTrusted(cert)) {
//			return false;
//		}
//		try {
//			PublicKey issuerPublicKey = ksUtil.getPublicKey(cert.getIssuer().getPublicKey());
//			X509Certificate x509cert = (X509Certificate)ksUtil.getKs().getCertificate(cert.getSerialNumber());
//			x509cert.verify(issuerPublicKey);
//		} catch (SignatureException | InvalidKeyException exceptionsForWhenCertificateIsInvalid) {
//			return true;
//		} catch (CertificateException | NoSuchAlgorithmException | NoSuchProviderException | KeyStoreException e) {
//			e.printStackTrace();
//			return true;
//		}
//		return false;
	}
}
