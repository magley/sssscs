package com.ib.verification;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ib.user.User;
import com.ib.verification.VerificationCode.Reason;

@Repository
public interface IVerificationCodeRepo extends JpaRepository<VerificationCode, Long> {
	Optional<VerificationCode> findByUserAndReason(User user, Reason reason);
}
