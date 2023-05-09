package com.ib.user;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ib.user.exception.EmailTakenException;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class UserService implements IUserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private IUserRepo userRepo;

	@Override
	public User register(User user) {
		if (isEmailTaken(user.getEmail())) {
			throw new EmailTakenException();
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return userRepo.save(user);
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
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepo.save(user);
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
		return Math.abs(minutesPassed) > 5;
	}
}
