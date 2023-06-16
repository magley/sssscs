package com.ib.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.ib.user.dto.PasswordRotationDto;

public interface IUserService extends UserDetailsService {
	User register(User user);

	User findById(Long issuer);

	User findByEmail(String userEmail);

	void verify(User user);
	
	void blockUserForAnHour(User user);
	
	void setLastTimeOf2FAToNow(User user);
	
	boolean isTimeFor2FA(User user);
	
	void unblockUser(User user);
	
	// Checks and updates.
	Boolean isBlocked(User user);
	
	void resetPassword(User user, String newPassword);

	boolean isTimeToChangePassword(User user);
	
	void rotatePassword(PasswordRotationDto dto);
}
