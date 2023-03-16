package com.ib.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ib.user.dto.UserCreateDto;
import com.ib.user.exception.EmailTakenException;

@Service
public class UserServiceImpl implements UserService {
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
	
}
