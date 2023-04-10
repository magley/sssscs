package com.ib.pki;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;

import org.springframework.beans.factory.annotation.Autowired;

import com.ib.certificate.Certificate;
import com.ib.user.IUserRepo;
import com.ib.user.IUserService;
import com.ib.user.User;

public class KeyPairUtil {
	private String secret = "MySecret47389473289";
	
	@Autowired
	private KeyStore ks;
	
	public KeyPair getOrCreate(User user) {
		PrivateKey privateKeyFromKeyStore = getPrivateKeyOrNull(user);
		
		if (privateKeyFromKeyStore == null) {
			KeyPair kp = generateKeyPair(user);
			setPrivateKey(user, kp.getPrivate());
			user.setPublicKey(kp.getPublic());
			return kp;
		} else {
			return new KeyPair(user.getPublicKey(), privateKeyFromKeyStore);
		}
	}
	
	private KeyPair generateKeyPair(User user) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keygen.initialize(2048, rand);
			KeyPair kp = keygen.generateKeyPair();
			return keygen.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private PrivateKey getPrivateKeyOrNull(User user) {
		try {
			if (ks.isKeyEntry(user.getEmail())) {
				return (PrivateKey) ks.getKey(user.getEmail(), secret.toCharArray());
			}
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void setPrivateKey(User user, PrivateKey privateKey) {
		try {
			java.security.cert.Certificate rootCertificate = ks.getCertificate("1"); // ID=1 -> root cert.
			ks.setKeyEntry(user.getEmail(), privateKey, secret.toCharArray(), new java.security.cert.Certificate[] {rootCertificate});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
