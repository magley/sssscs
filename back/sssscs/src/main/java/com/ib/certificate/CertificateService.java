package com.ib.certificate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.ib.certificate.Certificate.Status;
import com.ib.certificate.Certificate.Type;
import com.ib.certificate.dto.CertificateSummaryItemDto;
import com.ib.certificate.exception.CertificateAlreadyRevokedException;
import com.ib.certificate.exception.InvalidCertificateTypeException;
import com.ib.certificate.exception.RevocationUnauthorizedException;
import com.ib.certificate.request.CertificateRequest;
import com.ib.certificate.request.ICertificateRequestService;
import com.ib.pki.KeyUtil;
import com.ib.user.User;
import com.ib.user.User.Role;
import com.ib.util.exception.EntityNotFoundException;
import com.ib.util.security.IAuthenticationFacade;

@Service
public class CertificateService implements ICertificateService {
	@Autowired
	private ICertificateRepo certificateRepo;
	@Autowired
	private ICertificateRequestService certificateRequestService;
	@Autowired
	private KeyUtil keyUtil;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private IAuthenticationFacade auth;

	@Override
	public Certificate findById(Long id) {
		return certificateRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(Certificate.class, id));
	}
	
	@Override
	public List<Certificate> findByParent(Certificate parent) {
		return certificateRepo.findByParent(parent);
	}

	private boolean isAuthorizedToAcceptOrReject(CertificateRequest req, User user) {
		if (req.getParent() != null) {
			var requestsUserCanSign = certificateRequestService.findPendingRequestsIssuedTo(user);
			return requestsUserCanSign.contains(req);
		}
		return user.getRole() == Role.ADMIN;
	}

	@Override
	public Certificate accept(CertificateRequest req, User caller) {
		if (!isAuthorizedToAcceptOrReject(req, caller)) {
			throw new EntityNotFoundException(CertificateRequest.class, req.getId());
		}

		certificateRequestService.accept(req);

		Certificate c = Certificate.from(req);
		c = certificateRepo.save(c);
		createX509Certificate(c);
		return c;
	}

	@Override
	public void reject(CertificateRequest req, String reason, User caller) {
		if (!isAuthorizedToAcceptOrReject(req, caller)) {
			throw new EntityNotFoundException(CertificateRequest.class, req.getId());
		}

		certificateRequestService.reject(req, reason);
	}

	@Override
	public List<Certificate> getAll() {
		return certificateRepo.findAll();
	}

	@Override
	public boolean validate(Certificate cert) {
		if (isExpired(cert)) {
			return false;
		}
		if (isInvalidSignature(cert)) {
			return false;
		}
		if (isRevoked(cert)) {
			return false;
		}
		if (isTrusted(cert)) {
			return true;
		}
		return validate(cert.getParent());
	}

	@Override
	public List<CertificateSummaryItemDto> getAllSummary() {
		return getAll().stream().map(c -> modelMapper.map(c, CertificateSummaryItemDto.class)).toList();
	}

	@Override
	public boolean isValid(Certificate cert) {
		return validate(cert);
	}

	/**
	 * Create a X509Certificate object and save it into the file system.
	 * 
	 * @param c Entity representation of the certificate. <b>Must be saved in the
	 *          database</b> (because its ID is used).
	 * @return An X509Certificate that's been saved in the file system.
	 */
	private X509Certificate createX509Certificate(Certificate c) {
		Certificate parent = c.getParent();
		if (parent == null) {
			return createSelfSignedX509(c);
		} else {
			return createX509(c);
		}
	}

	/**
	 * Create a self-signed certificate (implied to be root), <b>and save it to the
	 * file system</b>.
	 * 
	 * @param c Entity representation of the certificate. Must be root.
	 * @return A new <code>X509Certificate</code>.
	 */
	private X509Certificate createSelfSignedX509(Certificate c) {
		KeyPair keyPair = keyUtil.generateKeyPair();

		X509Certificate x509 = generateSelfSigned(c, keyPair);
		keyUtil.saveX509Certificate(c.getSerialNumber(), x509);
		keyUtil.savePrivateKey(c.getSerialNumber(), keyPair.getPrivate());

		return x509;
	}

	/**
	 * Create a certificate (non-root, signed by the parent), <b>and save it to the
	 * file system</b>.
	 * 
	 * @param c Entity representation of the certificate. Must not be root.
	 * @return A new <code>X509Certificate</code>.
	 */
	private X509Certificate createX509(Certificate c) {
		Certificate parent = c.getParent();
		PrivateKey priv = keyUtil.getPrivateKey(parent.getSerialNumber());
		KeyPair keyPair = keyUtil.generateKeyPair();

		X509Certificate x509 = generate(c, keyPair.getPublic(), priv);
		keyUtil.saveX509Certificate(c.getSerialNumber(), x509);
		keyUtil.savePrivateKey(c.getSerialNumber(), keyPair.getPrivate());

		return x509;
	}

	/**
	 * Generate <code>X509Certificate</code>, non-root.
	 * 
	 * @param c          Entity representation of the certificate. Must not be root.
	 * @param subjectKey Public key of the subject.
	 * @param issuerKey  Private key of the issuer (used by parent certificate to
	 *                   sign this certificate).
	 * @return A new <code>X509Certificate</code>.
	 */
	private X509Certificate generate(Certificate c, PublicKey subjectKey, PrivateKey issuerKey) {
		if (c.getType() == Type.ROOT) {
			throw new InvalidCertificateTypeException(c);
		}

		try {
			JcaContentSignerBuilder csbuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			csbuilder = csbuilder.setProvider("BC");
			ContentSigner signer = csbuilder.build(issuerKey);

			final X500Name issuerX500Name = keyUtil.getX500Name(c.getParent().getOwner());
			final X500Name subjectX500Name = c.getSubjectData().getX500Name();
			X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(issuerX500Name,
					BigInteger.valueOf(c.getId()), keyUtil.dateFrom(c.getValidFrom()), keyUtil.dateFrom(c.getValidTo()),
					subjectX500Name, subjectKey);

			X509CertificateHolder holder = builder.build(signer);
			JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
			conv = conv.setProvider("BC");
			return conv.getCertificate(holder);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Generate self-signed <code>X509Certificate</code>, root.
	 * 
	 * @param c  Entity representation of the certificate. Must be root.
	 * @param kp Newly generated pair of keys used to sign and verify this
	 *           certificate.
	 * @return A new <code>X509Certificate</code>.
	 */
	private X509Certificate generateSelfSigned(Certificate c, KeyPair kp) {
		if (c.getType() != Type.ROOT) {
			throw new InvalidCertificateTypeException(c);
		}

		try {
			JcaContentSignerBuilder csbuilder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			csbuilder = csbuilder.setProvider("BC");
			ContentSigner signer = csbuilder.build(kp.getPrivate());

			final X500Name x500Name = new X500Name("CN=localhost");
			X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(x500Name, BigInteger.valueOf(c.getId()),
					keyUtil.dateFrom(c.getValidFrom()), keyUtil.dateFrom(c.getValidTo()), x500Name, kp.getPublic());

			X509CertificateHolder holder = builder.build(signer);
			JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
			conv = conv.setProvider("BC");
			return conv.getCertificate(holder);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
	}
	
	private boolean isRevoked(Certificate cert) {
		return cert.getStatus() == Status.REVOKED;
	}

	private boolean isInvalidSignature(Certificate cert) {
		X509Certificate self509 = keyUtil.getX509Certificate(cert.getSerialNumber());
		X509Certificate parent509 = null;

		if (isTrusted(cert)) {
			parent509 = self509;
		} else {
			parent509 = keyUtil.getX509Certificate(cert.getParent().getSerialNumber());
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

	@Transactional
	@Override
	public void revoke(Long certificateId, String revocationReason) {
		Certificate certificate = this.findById(certificateId);
		User user = auth.getUser();
		if (user.getRole() != Role.ADMIN && certificate.getOwner().getId() != user.getId()) {
			throw new RevocationUnauthorizedException();
		}
		if (certificate.getStatus() == Status.REVOKED) {
			throw new CertificateAlreadyRevokedException();
		}
		this.revoke(certificate, revocationReason);
	}

	private void revoke(Certificate certificate, String revocationReason) {
		if (certificate.getStatus() == Status.REVOKED) {
			return;
		}
		if (certificate.getType() != Type.END) {
			this.findByParent(certificate).forEach(c -> this.revoke(c, revocationReason));
		}
		certificate.setStatus(Status.REVOKED);
		certificate.setRevocationReason(revocationReason);
		certificateRepo.save(certificate);
		certificateRequestService.findByParent(certificate).forEach(c -> certificateRequestService.reject(c, revocationReason));
	}

	@Override
	public FileSystemResource download(Long certificateId) {
		Certificate cert = findById(certificateId);
		// TODO: check if it is possible for file to not exist while db has it
		return new FileSystemResource(keyUtil.getFnameCert(cert.getSerialNumber()));
	}

	@Override
	public FileSystemResource downloadPrivate(Long certificateId) {
		Certificate cert = findById(certificateId);
		if (cert.getOwner().getId() == null || !cert.getOwner().getId().equals(auth.getUser().getId())) {
			throw new EntityNotFoundException(Certificate.class, certificateId);
		}
		return new FileSystemResource(keyUtil.getFnamePrivKey(cert.getSerialNumber()));
	}

	@Override
	public boolean isValid(MultipartFile certFile) {
		Long id = getCertId(certFile);
		Certificate cert = findById(id);
		checkIfOwnCertificateFile(certFile, cert);
		return isValid(cert);
	}
	
	// TODO: maybe rename?
	private void checkIfOwnCertificateFile(MultipartFile certFile, Certificate cert) {
		try (InputStream certStream = certFile.getInputStream()) {
			boolean equal = IOUtils.contentEquals(certStream, new FileInputStream(keyUtil.getFnameCert(cert.getSerialNumber())));
			if (!equal) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uploaded certificate was tampered with.");
			}
		} catch (FileNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a certificate file we own.");
		} catch (IOException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown error when reading cert file from our disk.");
		}
	}
	
	private Long getCertId(MultipartFile certFile) {
		X509Certificate uploadedCert;
		try (InputStream certStream = certFile.getInputStream()) {
			uploadedCert = keyUtil.generateX509Certificate(certStream);
		} catch (CertificateException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a valid certificate file.");
		} catch (IOException e1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown error when reading your file.");
		}
		return uploadedCert.getSerialNumber().longValue(); // TODO: can this bigInt to long conversion cause problems?
	}
}
