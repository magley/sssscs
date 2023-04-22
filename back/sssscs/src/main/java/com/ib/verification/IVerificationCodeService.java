package com.ib.verification;

import com.ib.user.User;
import com.ib.verification.dto.VerificationCodeSendRequestDto.Method;

public interface IVerificationCodeService {
	VerificationCode getOrCreateCode(User user);
	void sendCode(VerificationCode code, Method method);
}
