package com.ib.certificate;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ib.certificate.CertificateRequest.Status;

@Repository
public interface ICertificateRequestRepo extends JpaRepository<CertificateRequest, Long> {
	public Optional<CertificateRequest> findByIdAndStatusEquals(Long id, Status status);
}
