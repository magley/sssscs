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

import com.ib.pki.manual.KeyUtil;
import com.ib.user.dto.UserCreateDto;
import com.ib.user.dto.UserLoginDto;
import com.ib.util.DTO;
import com.ib.util.security.JwtTokenUtil;

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
	private KeyUtil keyUtil;

	@PostMapping
	public ResponseEntity<User> register(@DTO(UserCreateDto.class) User user) {
		return ResponseEntity.ok(userService.register(user));
	}
	
	@PutMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserLoginDto dto) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());
		Authentication auth = null;
		try {
			auth = authManager.authenticate(authToken);
		} catch (BadCredentialsException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong email or password!");
		}
		User user = (User) auth.getPrincipal();
		
		
		// For testing.
		System.err.println("UserController::login()");
		System.err.println(keyUtil.readPrivateKey(user));
		System.err.println(keyUtil.readPublicKeyFromStr(user.getPublicKey()));
		
		// TODO: json mb
		String token = jwtTokenUtil.generateToken(user.getEmail(), user.getId(), user.getRole().toString());
		return ResponseEntity.ok(token);
	}
}
