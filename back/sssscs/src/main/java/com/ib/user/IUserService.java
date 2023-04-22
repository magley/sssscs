package com.ib.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
	User register(User user);
	User findById(Long issuer);
	User findByEmail(String userEmail);
}
