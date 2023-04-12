package com.ib.pki;

import org.springframework.beans.factory.annotation.Autowired;

public class KeyPairUtil {
	@Autowired
	private KeyStoreUtil ksUtil;
	
//	public KeyPair getOrCreateNewFor(User user) {
//		PrivateKey privateKeyFromKeyStore = getPrivateKeyOrNull(user);
//		
//		if (privateKeyFromKeyStore == null) {
//			KeyPair kp = generateKeyPair(user);
//			savePrivateKeyToKeyStore(user, kp.getPrivate());
//			return kp;
//		} else {
//			return new KeyPair(ksUtil.getPublicKey(user.getPublicKey()), privateKeyFromKeyStore);
//		}
//	}
//	
//	private PrivateKey getPrivateKeyOrNull(User user) {
//		return ksUtil.getPrivateKeyOrNull(user.getEmail());
//	}
//	
//	private void savePrivateKeyToKeyStore(User user, PrivateKey privateKey) {
//		ksUtil.setPrivateKey(user.getEmail(), privateKey);
//	}
//	
//	private KeyPair generateKeyPair(User user) {
//		try {
//			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
//			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
//			keygen.initialize(2048, rand);
//			return keygen.generateKeyPair();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
