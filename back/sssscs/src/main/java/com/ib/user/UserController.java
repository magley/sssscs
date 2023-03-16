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

	@PostMapping("/api/user")
	public ResponseEntity<?> register(@RequestBody UserCreateDto dto) {
		User user = userService.register(dto);
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
}
