package com.ib.user;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.ib.util.DTO;
import com.ib.util.security.JwtTokenUtil;

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

	@PostMapping("/session/register")
	public ResponseEntity<?> register(@DTO(UserCreateDto.class) User user) {
		userService.register(user);
		return new ResponseEntity<Void>((Void) null, HttpStatus.NO_CONTENT);
	}

	@PostMapping("/session/login")
	public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto dto) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(),
				dto.getPassword());
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password!");
		}

		User user = (User) auth.getPrincipal();
		if (userService.isBlocked(user)) {
			// TOO_MANY_REQUESTS == 429, it stands out which helps us on the frontend.
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "You are blocked.");
		}
		if (user.getVerified() == false || userService.isTimeFor2FA(user)) {
			// UNPROCESSABLE_ENTITY == 422, it stands out which helps us on the frontend.
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Verify your account.");
		}
		if (userService.isTimeToChangePassword(user)) {
			// NOT_ACCEPTABLE == 406, it stands out which helps us on the frontend.
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Rotate your password");
		}
		
		String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
		return ResponseEntity.ok(token);
	}
	
	@PostMapping("/rotate-password")
	public ResponseEntity<?> rotate_password(@Valid @RequestBody PasswordRotationDto dto) {
		userService.rotatePassword(dto);
		return new ResponseEntity<>(0L, HttpStatus.OK);
	}
}
