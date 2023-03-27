package com.ib.certificate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ib.certificate.CertificateRequest.Status;
import com.ib.user.User;

@Repository
public interface ICertificateRequestRepo extends JpaRepository<CertificateRequest, Long> {
	public Optional<CertificateRequest> findByIdAndStatus(Long id, Status status);
	public List<CertificateRequest> findByIssuer(User issuer);
	
	@Query("select r from CertificateRequest r left join r.parent c where ((c.issuer.id = :issueeId) or (:includeEmpty = true and c = null))")
	public List<CertificateRequest> findByIssuee(@Param("issueeId") Long issueeId, @Param("includeEmpty") boolean includeEmpty);
}
