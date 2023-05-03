package com.ib.certificate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ib.user.User;

@Repository
public interface ICertificateRepo extends JpaRepository<Certificate, Long> {
	
	Optional<Certificate> findByIdAndOwner(Long id, User owner);
	
	List<Certificate> findByParent(Certificate parent);
}
