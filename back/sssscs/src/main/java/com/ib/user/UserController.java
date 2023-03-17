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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ib.user.dto.UserCreateDto;
import com.ib.user.dto.UserLoginDto;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private IUserService userService;
	@Autowired
	private AuthenticationManager authManager;
	
	@PostMapping
	public ResponseEntity<User> register(@RequestBody UserCreateDto dto) {
		User user = userService.register(dto);
		return ResponseEntity.ok(user);
	}
	
	@PutMapping("/login")
	public ResponseEntity<User> login(@RequestBody UserLoginDto dto) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
	
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password!");
		};
		
		// TODO: Generate token/cookie.
		// TODO: Return token instead of User.
		
		User user = new User();
		user.setEmail(dto.getEmail());
		
		return ResponseEntity.ok(user);
	}
}
