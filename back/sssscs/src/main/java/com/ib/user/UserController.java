package com.ib.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ib.user.dto.UserCreateDto;
import com.ib.user.dto.UserLoginDto;

import jakarta.annotation.security.PermitAll;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationManager authManager;
	
	@PostMapping("/api/user")
	public ResponseEntity<?> register(@RequestBody UserCreateDto dto) {
		User user = userService.register(dto);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	
	@PutMapping("/api/user/login")
	public ResponseEntity<?> login(@RequestBody UserLoginDto dto) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
	
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			return new ResponseEntity<>("Wrong email or password!", HttpStatus.BAD_REQUEST);
		};
		
		// TODO: Set authentication in SecurityContext.
		// TODO: Generate token/cookie.
		
		return new ResponseEntity<>(new User(), HttpStatus.OK);
	}
}
