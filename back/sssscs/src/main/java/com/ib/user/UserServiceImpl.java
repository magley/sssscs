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
public class UserServiceImpl implements UserService, UserDetailsService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private IUserRepo userRepo;

	@Override
	public User register(UserCreateDto dto) {
		if (isEmailTaken(dto.getEmail())) {
			throw new EmailTakenException();
		}
		
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user.setName(dto.getName());
		user.setSurname(dto.getSurname());
		
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
			// TODO: Does this ever get thrown? It seems that by default, Spring prefers throwing
			// the more generic BadCredentialsException, which we have to catch when invoking
			// AuthenticationManager.authenticate(), which makes sense because it's safer not to
			// know which credential (email/password) is incorrect.
			throw new UsernameNotFoundException("User not found with this username: " + email);
		}
	}
}
