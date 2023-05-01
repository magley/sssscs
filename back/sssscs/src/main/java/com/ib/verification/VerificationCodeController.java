package com.ib.verification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ib.user.IUserService;
import com.ib.user.User;
import com.ib.verification.dto.VerificationCodeSendRequestDto;
import com.ib.verification.dto.VerificationCodeVerifyDTO;

import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/verification-code")
public class VerificationCodeController {
	@Autowired
	private IVerificationCodeService service;
	@Autowired
	private IUserService userService;

	@PermitAll
	@PostMapping("/send")
	public ResponseEntity<?> sendVerificationCode(@Valid @RequestBody VerificationCodeSendRequestDto dto) {
		User user = userService.findByEmail(dto.getUserEmail());
		VerificationCode code = service.getOrCreateCode(user);

		if (dto.getDontActuallySend() == false) {
			service.sendCode(code, dto.getMethod());
		}

		System.err.println(dto);
		System.err.println(code.getCode());
		System.err.println(code.getExpiration());

		return new ResponseEntity<Void>((Void) null, HttpStatus.NO_CONTENT);
	}

	@PermitAll
	@PostMapping("/verify")
	public ResponseEntity<?> verifyUser(@Valid @RequestBody VerificationCodeVerifyDTO dto) {
		service.verifyUser(dto);
		return new ResponseEntity<Void>((Void) null, HttpStatus.NO_CONTENT);
	}
}