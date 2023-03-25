package com.ib.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICertificateRequestRepo extends JpaRepository<CertificateRequest, Long> {

}
