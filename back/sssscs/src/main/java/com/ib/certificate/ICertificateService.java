package com.ib.certificate;

import java.util.List;

import com.ib.certificate.dto.CertificateSummaryItemDto;
import com.ib.certificate.request.CertificateRequest;

public interface ICertificateService {
	public Certificate findById(Long id);
	public Certificate accept(CertificateRequest req);
	public List<Certificate> getAll();
	
	public boolean validate(Certificate cert);
	public List<CertificateSummaryItemDto> getAllSummary();
	public boolean isValid(Certificate cert);
}
