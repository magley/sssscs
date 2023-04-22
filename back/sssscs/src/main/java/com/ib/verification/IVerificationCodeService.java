package com.ib.verification;

import com.ib.user.User;
import com.ib.verification.dto.VerificationCodeSendRequestDto.Method;
import com.ib.verification.dto.VerificationCodeVerifyDTO;

import jakarta.validation.Valid;

public interface IVerificationCodeService {
	VerificationCode getOrCreateCode(User user);

	VerificationCode get(User user);

	void sendCode(VerificationCode code, Method method);

	void verifyUser(@Valid VerificationCodeVerifyDTO dto);
}
