package com.ib.verification;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.user.IUserService;
import com.ib.user.User;
import com.ib.util.twilio.SendgridUtil;
import com.ib.verification.VerificationCode.Reason;
import com.ib.verification.dto.VerificationCodeResetDto;
import com.ib.verification.dto.VerificationCodeSendRequestDto.Method;
import com.ib.verification.dto.VerificationCodeVerifyDTO;
import com.ib.verification.exception.InvalidCodeException;
import com.ib.verification.exception.UnsupportedVerificationSendMethodException;
import com.ib.verification.exception.VerificationAttemptPenaltyException;
import com.ib.verification.exception.VerificationCodeNotFoundException;

import jakarta.validation.Valid;

@Service
public class VerificationCodeService implements IVerificationCodeService {
	@Autowired
	private IVerificationCodeRepo repo;
	@Autowired
	private SendgridUtil sendgridUtil;
	@Autowired
	private IUserService userService;
	
	private static final Long MAX_ATTEMPTS = 3L;

	@Override
	public VerificationCode getOrCreateCode(User user, Reason reason) {
		try {
			return get(user, reason, null);
		} catch (VerificationCodeNotFoundException e) {
			return generateCode(user, reason);
		}
	}

	private VerificationCode generateCode(User user, Reason reason) {
		Random rnd = new Random();
		String codeStr = String.format("%06d", rnd.nextInt(999999));

		VerificationCode code = new VerificationCode(null, codeStr, LocalDateTime.now().plusSeconds(30), user, MAX_ATTEMPTS, reason);
		return repo.save(code);
	}

	private Boolean isExpired(VerificationCode code) {
		return code.getExpiration().isBefore(LocalDateTime.now());
	}

	@Override
	public void sendCode(VerificationCode code, Method method) {
		switch (method) {
		case EMAIL:
			sendgridUtil.sendEmail("sssscs activation code", getCodeText(code.getCode()));
			break;
		case SMS:
			sendgridUtil.sendSMS(getCodeText(code.getCode()));
			break;
		default:
			throw new UnsupportedVerificationSendMethodException(method);
		}
	}

	private String getCodeText(String code) {
		return "Your code is <b>" + code + "</b>";
	}

	@Override
	public void verifyUser(@Valid VerificationCodeVerifyDTO dto) {
		User user = userService.findByEmail(dto.getUserEmail());
		VerificationCode code = get(user, Reason.TWO_FA, dto.getCode());

		userService.verify(user);
		userService.setLastTimeOf2FAToNow(user);
		repo.delete(code);
	}

	@Override
	public VerificationCode get(User user, Reason reason, String codeStr) {
		VerificationCode code = repo.findByUserAndReason(user, reason).orElseThrow(() -> new VerificationCodeNotFoundException(user));
		if (isExpired(code)) {
			repo.delete(code);
			code = get(user, reason, codeStr);
		}
		if (codeStr != null && !code.getCode().equals(codeStr)) {
			decrementAttemptsLeft(user, code);
			throw new InvalidCodeException();
		}
		return code;
	}
	
	// Will block user.
	private void decrementAttemptsLeft(User user, VerificationCode code) throws VerificationAttemptPenaltyException {
		code.setAttemptsLeft(code.getAttemptsLeft() - 1);
		repo.save(code);
		if (code.getAttemptsLeft() <= 0) {
			userService.blockUserForAnHour(user);
			throw new VerificationAttemptPenaltyException();
		}
	}

	@Override
	public void resetPassword(VerificationCodeResetDto dto) {
		User user = userService.findByEmail(dto.getUserEmail());
		VerificationCode code = get(user, Reason.RESET_PASSWORD, dto.getCode());
		userService.resetPassword(user, dto.getNewPassword());
	}
}
