package com.ib.user;

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
		return userRepo.findByEmail(email).orElseThrow(
				() -> new UsernameNotFoundException("User not found with this username: " + email));
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
	}
}
