package com.ib.certificate.request;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ib.certificate.request.CertificateRequest.Status;
import com.ib.user.User;

@Repository
public interface ICertificateRequestRepo extends JpaRepository<CertificateRequest, Long> {
	public Optional<CertificateRequest> findByIdAndStatus(Long id, Status status);
	public List<CertificateRequest> findByCreator(User creator);
	@Query("select r from CertificateRequest r left join r.parent c where (((c.owner.id = :issueeId) or (:includeEmpty = true and c = null)) and r.status = 'PENDING')")
	public List<CertificateRequest> findPendingRequestsIssuedTo(Long issueeId, boolean includeEmpty);
}
