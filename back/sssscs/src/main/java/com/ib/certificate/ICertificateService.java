package com.ib.certificate;

import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.FileSystemResource;

import com.ib.certificate.dto.CertificateSummaryItemDto;
import com.ib.certificate.request.CertificateRequest;
import com.ib.user.User;

public interface ICertificateService {
	public Certificate findById(Long id);
	
	public List<Certificate> findByParent(Certificate parent);

	public Certificate accept(CertificateRequest req, User caller);

	public void reject(CertificateRequest req, String reason, User caller);

	public List<Certificate> getAll();

	public boolean validate(Certificate cert);

	public List<CertificateSummaryItemDto> getAllSummary();

	public boolean isValid(Certificate cert);
	
	public void revoke(Long certificateId, String revocationReason);

	public FileSystemResource download(Long certificateId);

	public boolean isValid(InputStream certStream);
}
