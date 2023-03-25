package com.ib.certificate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ib.certificate.CertificateRequest.Status;
import com.ib.user.User;

@Repository
public interface ICertificateRequestRepo extends JpaRepository<CertificateRequest, Long> {
	public Optional<CertificateRequest> findByIdAndStatusEquals(Long id, Status status);
	public List<CertificateRequest> findByIssuer(User issuer);
}
