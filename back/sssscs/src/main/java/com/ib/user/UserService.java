package com.ib.user;

import java.security.KeyPair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ib.pki.KeyPairUtil;
import com.ib.pki.KeyStoreUtil;
import com.ib.user.exception.EmailTakenException;
import com.ib.util.exception.EntityNotFoundException;

@Service
public class UserService implements IUserService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private IUserRepo userRepo;
	@Autowired
	private KeyPairUtil keyPairUtil;
	
	@Autowired
	private KeyStoreUtil ksUtil;
	
	@Override
	public User register(User user) {
		if (isEmailTaken(user.getEmail())) {
			throw new EmailTakenException();
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		KeyPair kp = keyPairUtil.getOrCreateNewFor(user);
		user.setPublicKey(kp.getPublic().toString());
		
		System.err.println(user.getPublicKey().toString());
		System.err.println(ksUtil.getPrivateKeyOrNull(user.getEmail()));
		
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
