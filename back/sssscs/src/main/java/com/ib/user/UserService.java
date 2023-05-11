package com.ib.user;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ib.user.dto.PasswordRotationDto;
import com.ib.user.exception.EmailTakenException;
import com.ib.user.exception.PasswordTooRecentException;
import com.ib.user.exception.WrongPasswordException;
import com.ib.util.exception.EntityNotFoundException;
import com.ib.util.security.PasswordUtil;

@Service
public class UserService implements IUserService {
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private PasswordUtil passwordUtil;
	private static final Integer MAX_PREVIOUS_PASSWORDS_SAVED = 5;

	@Override
	public User register(User user) {
		if (isEmailTaken(user.getEmail())) {
			throw new EmailTakenException();
		}
		return setNewPassword(user, user.getPassword());
	}

	private boolean isEmailTaken(String email) {
		return userRepo.findByEmail(email).isPresent();
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return userRepo.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with this username: " + email));
	}

	@Override
	public User findById(Long issuer) {
		return userRepo.findById(issuer).orElseThrow(() -> new EntityNotFoundException(User.class, issuer));
	}

	@Override
	public User findByEmail(String userEmail) {
		return userRepo.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException(User.class, userEmail));
	}

	@Override
	public void verify(User user) {
		user.setVerified(true);
		userRepo.save(user);
	}
	
	@Override
	public void resetPassword(User user, String newPassword) {
		setNewPassword(user, newPassword);
	}

	@Override
	public void blockUserForAnHour(User user) {
		user.setBlocked(true);
		user.setBlockEndDate(LocalDateTime.now().plusHours(1L));
		userRepo.save(user);
	}
	
	@Override 
	public void unblockUser(User user) {
		user.setBlocked(false);
		user.setBlockEndDate(null);
		userRepo.save(user);
	}

	@Override
	public Boolean isBlocked(User user) {
		if (user.getBlocked() == false) {
			return false;
		}
		if (user.getBlockEndDate().isBefore(LocalDateTime.now())) {
			unblockUser(user);
			return false;
		}
		return true;
	}

	@Override
	public void setLastTimeOf2FAToNow(User user) {
		user.setLastTimeOf2FA(LocalDateTime.now());
		userRepo.save(user);
	}

	@Override
	public boolean isTimeFor2FA(User user) {
		LocalDateTime lastTime = user.getLastTimeOf2FA();
		
		if (lastTime == null) {
			return true;
		}
		
		LocalDateTime now = LocalDateTime.now();
		
		long minutesPassed = ChronoUnit.MINUTES.between(lastTime, now);
		return minutesPassed >= 5;
	}
	
	@Override
	public boolean isTimeToChangePassword(User user) {
		LocalDateTime lastTime = user.getLastTimeOfPasswordChange();
		
		if (lastTime == null) {
			return true;
		}
		
		LocalDateTime now = LocalDateTime.now();
		
		long minutesPassed = ChronoUnit.MINUTES.between(lastTime, now);
		return minutesPassed >= 1;
	}

	@Override
	public void rotatePassword(PasswordRotationDto dto) {
		User user = findByEmail(dto.getUserEmail());
		
		if (!passwordUtil.doPasswordsMatch(dto.getOldPassword(), user.getPassword())) {
			throw new WrongPasswordException();
		}
		
		if (isPasswordTooRecent(user, dto.getNewPassword())) {
			throw new PasswordTooRecentException();
		}

		setNewPassword(user, dto.getNewPassword());
	}
	
	/**
	 * SIDE EFFECT: Saves `user` to the database.
	 */
	private User setNewPassword(User user, String passwordPlaintext) {
		user.setLastTimeOfPasswordChange(LocalDateTime.now());
		user.setPassword(passwordUtil.encode(passwordPlaintext));
		
		while (user.getLastNPasswords().size() >= MAX_PREVIOUS_PASSWORDS_SAVED) {
			user.getLastNPasswords().remove(0);
		}
		user.getLastNPasswords().add(user.getPassword());
		
		return userRepo.save(user);
	}
	
	private boolean isPasswordTooRecent(User user, String passwordPlaintext) {
		if (passwordUtil.doPasswordsMatch(passwordPlaintext, user.getPassword())) {
			return true;
		}
		
		for (String oldPassword : user.getLastNPasswords()) {
			if (passwordUtil.doPasswordsMatch(passwordPlaintext, oldPassword)) {
				return true;
			}
		}
		return false;
	}
}
