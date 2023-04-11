package com.ib.pki;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;

import com.ib.user.User;

public class KeyPairUtil {
	@Autowired
	private KeyStoreUtil ksUtil;
	
	@Autowired
	private KeyStore ks;
	
	public KeyPair getOrCreateNewFor(User user) {
		PrivateKey privateKeyFromKeyStore = getPrivateKeyOrNull(user);
		
		if (privateKeyFromKeyStore == null) {
			KeyPair kp = generateKeyPair(user);
			savePrivateKeyToKeyStore(user, kp.getPrivate());
			return kp;
		} else {
			return new KeyPair(user.getPublicKey(), privateKeyFromKeyStore);
		}
	}
	
	private PrivateKey getPrivateKeyOrNull(User user) {
		return ksUtil.getPrivateKeyOrNull(user.getEmail());
	}
	
	private void savePrivateKeyToKeyStore(User user, PrivateKey privateKey) {
		ksUtil.setPrivateKey(user.getEmail(), privateKey);
	}
	
	private KeyPair generateKeyPair(User user) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keygen.initialize(2048, rand);
			return keygen.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
