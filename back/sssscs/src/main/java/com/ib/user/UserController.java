package com.ib.user;

import com.ib.user.dto.UserCreateDto;
import com.ib.user.dto.UserLoginDto;
import com.ib.util.DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/user")
public class UserController {
	@Autowired
	private IUserService userService;
	@Autowired
	private AuthenticationManager authManager;
	
	@PostMapping
	public ResponseEntity<User> register(@DTO(UserCreateDto.class) User user) {
		// FIXME: Return something other than user. Also, why do we return 500 with
		//        "Failed to write request" now that User implements UserDetails?
		return ResponseEntity.ok(userService.register(user));
	}
	
	@PutMapping("/login")
	public ResponseEntity<User> login(@RequestBody UserLoginDto dto) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
	
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password!");
		}
		
		// TODO: Generate token/cookie.
		// TODO: Return token instead of User.
		
		User user = new User();
		user.setEmail(dto.getEmail());
		
		return ResponseEntity.ok(user);
	}
}
