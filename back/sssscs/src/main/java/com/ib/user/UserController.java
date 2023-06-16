package com.ib.user;

import com.ib.util.recaptcha.ReCAPTCHAUtil;

import java.time.LocalDateTime;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ib.user.dto.PasswordRotationDto;
import com.ib.user.dto.UserCreateDto;
import com.ib.user.dto.UserLoginDto;
import com.ib.util.security.JwtTokenUtil;
import com.ib.util.security.LoggerInterceptor;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private IUserService userService;
	@Autowired
	private AuthenticationManager authManager;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private ReCAPTCHAUtil captchaUtil;
	
	private static Logger log = LoggerFactory.getLogger(LoggerInterceptor.class);

	@PostMapping("/session/register")
	public ResponseEntity<?> register(@Valid @RequestBody UserCreateDto dto) {
		int logId = new Random().nextInt();
		log.info("[Register {}] Begin", logId);
		
		captchaUtil.processResponse(dto.getToken());
		userService.register(mapper.map(dto, User.class));
		
		log.info("[Register {}] Finish", logId);
		return new ResponseEntity<Void>((Void) null, HttpStatus.NO_CONTENT);
	}

	@PostMapping("/session/login")
	public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto dto) {
		int logId = new Random().nextInt();
		log.info("[Login {}] Begin", logId);
		
		captchaUtil.processResponse(dto.getToken());
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			log.info("[Login {}] Bad credentials", logId);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password!");
		}

		User user = (User) auth.getPrincipal();
		if (userService.isBlocked(user)) {
			// TOO_MANY_REQUESTS == 429, it stands out which helps us on the frontend.
			log.info("[Login {}] User blocked", logId);
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "You are blocked.");
		}
		if (user.getVerified() == false || userService.isTimeFor2FA(user)) {
			// UNPROCESSABLE_ENTITY == 422, it stands out which helps us on the frontend.
			log.info("[Login {}] Needs verification", logId);
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Verify your account.");
		}
		if (userService.isTimeToChangePassword(user)) {
			// NOT_ACCEPTABLE == 406, it stands out which helps us on the frontend.
			log.info("[Login {}] Stale password", logId);
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Rotate your password");
		}
		
		String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
		log.info("[Login {}] Logged in user {}", logId, user.getId());
		return ResponseEntity.ok(token);
	}
	
	@PostMapping("/rotate-password")
	public ResponseEntity<?> rotate_password(@Valid @RequestBody PasswordRotationDto dto) {
		userService.rotatePassword(dto);
		return new ResponseEntity<>(0L, HttpStatus.OK);
	}
}
