package com.ib.user;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface IUserService extends UserDetailsService {
	User register(User user);

	User findById(Long issuer);

	User findByEmail(String userEmail);

	void verify(User user);
	
	void resetLoginCounter(User user);
	
	void incrementLoginCounter(User user);
	
	void blockUserForAnHour(User user);
	
	void unblockUser(User user);
	
	// Checks and updates.
	Boolean isBlocked(User user);
}
