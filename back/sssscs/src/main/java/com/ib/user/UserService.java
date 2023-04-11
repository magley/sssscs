package com.ib.user;

import java.security.KeyPair;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ib.pki.manual.KeyUtil;
import com.ib.user.exception.EmailTakenException;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class UserService implements IUserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private KeyUtil keyUtil;
	
	@Override
	public User register(User user) {
		if (isEmailTaken(user.getEmail())) {
			throw new EmailTakenException();
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		KeyPair kp = keyUtil.getOrCreateNewFor(user); // Will always create new keys since the user is new.
		
		user.setPublicKey(keyUtil.writePublicKeyToStr(kp.getPublic()));
		keyUtil.writePrivateKey(kp.getPrivate(), user);
		
		// For testing.
		System.err.println("UserService::register()");
		System.err.println(kp.getPrivate());
		System.err.println(kp.getPublic());

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
}
