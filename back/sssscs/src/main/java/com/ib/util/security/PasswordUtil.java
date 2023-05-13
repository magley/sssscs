package com.ib.util.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public String encode(String plaintextPassword) {
		return passwordEncoder.encode(plaintextPassword);
	}
	
	public boolean doPasswordsMatch(String plaintextPassword, String encodedPassword) {
		return passwordEncoder.matches(plaintextPassword, encodedPassword);
	}
}
