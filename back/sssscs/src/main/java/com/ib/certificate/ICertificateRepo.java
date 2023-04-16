package com.ib.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICertificateRepo extends JpaRepository<Certificate, Long> {
}
