package com.ib.verification;

import com.ib.user.User;
import com.ib.verification.VerificationCode.Reason;
import com.ib.verification.dto.VerificationCodeResetDto;
import com.ib.verification.dto.VerificationCodeSendRequestDto.Method;
import com.ib.verification.dto.VerificationCodeVerifyDTO;

import jakarta.validation.Valid;

public interface IVerificationCodeService {
	VerificationCode getOrCreateCode(User user, Reason reason);

	VerificationCode get(User user, Reason reason, String codeStr);

	void sendCode(VerificationCode code, Method method);

	void verifyUser(@Valid VerificationCodeVerifyDTO dto);
	
	void resetPassword(VerificationCodeResetDto dto);
}
