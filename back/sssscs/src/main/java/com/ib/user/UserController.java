package com.ib.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ib.user.dto.UserCreateDto;

import jakarta.annotation.security.PermitAll;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
	@PostMapping("/api/user")
	public ResponseEntity<?> register(@RequestBody UserCreateDto dto) {
		User user = userService.register(dto);
		if (user == null) {
			return new ResponseEntity<>("Email already taken!", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<>(user, HttpStatus.OK);
		}
	}
}
