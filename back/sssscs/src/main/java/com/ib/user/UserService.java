package com.ib.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ib.user.dto.UserCreateDto;
import com.ib.user.exception.EmailTakenException;

@Service
public class UserService implements IUserService, UserDetailsService {
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
		Optional<User> user = userRepo.findByEmail(email);
		if (user.isPresent()) {
			return org.springframework.security.core.userdetails.User
					.withUsername(email)
					.password(user.get().getPassword())
					.roles("ADMIN") // TODO: Add actual roles.
					.build();
		} else {
			throw new UsernameNotFoundException("User not found with this username: " + email);
		}
	}
}
