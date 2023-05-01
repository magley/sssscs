package com.ib.verification;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.user.IUserService;
import com.ib.user.User;
import com.ib.util.twilio.SendgridUtil;
import com.ib.verification.dto.VerificationCodeSendRequestDto.Method;
import com.ib.verification.dto.VerificationCodeVerifyDTO;
import com.ib.verification.exception.InvalidCodeException;
import com.ib.verification.exception.UnsupportedVerificationSendMethodException;
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

	@Override
	public VerificationCode getOrCreateCode(User user) {
		Optional<VerificationCode> codeOpt = repo.findByUserAndValidTrue(user);

		if (codeOpt.isEmpty()) {
			return generateCode(user);
		} else {
			VerificationCode code = codeOpt.get();

			if (isExpired(code)) {
				code.setValid(false);
				repo.save(code);
				return generateCode(user);
			} else {
				return code;
			}
		}
	}

	private VerificationCode generateCode(User user) {
		Random rnd = new Random();
		String codeStr = String.format("%06d", rnd.nextInt(999999));

		VerificationCode code = new VerificationCode(null, codeStr, LocalDateTime.now().plusMinutes(30), user, true);
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
		VerificationCode code = get(user);

		if (!code.getCode().equals(dto.getCode())) {
			throw new InvalidCodeException();
		}

		userService.verify(user);
		userService.resetLoginCounter(user);
		code.setValid(false);
		repo.save(code);
	}

	@Override
	public VerificationCode get(User user) {
		return repo.findByUserAndValidTrue(user).orElseThrow(() -> new VerificationCodeNotFoundException(user));
	}
}
