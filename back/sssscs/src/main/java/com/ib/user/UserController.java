package com.ib.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ib.user.dto.UserCreateDto;
import com.ib.user.dto.UserLoginDto;
import com.ib.util.DTO;
import com.ib.util.security.JwtTokenUtil;

import jakarta.annotation.security.PermitAll;
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
		return new ResponseEntity<Void>((Void)null, HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/session/login")
	public ResponseEntity<String> login(@Valid @RequestBody UserLoginDto dto) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password!");
		}

		User user = (User) auth.getPrincipal();
		
		// TODO: user not verified OR server deemed it necessary to fire 2FA.
		if (user.getVerified() == false) {
			// Status 422, instead of 403, makes it easier to redirect on the front-end
			// because normally we would never use 422 so it stands out.
			throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Verify your account.");
		}
		
		String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
		
		return ResponseEntity.ok(token);
	}
}
