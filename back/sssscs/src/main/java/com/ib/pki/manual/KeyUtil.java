package com.ib.pki.manual;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.ib.certificate.Certificate;
import com.ib.user.User;

public class KeyUtil {
	private static String DIR_KEYS = "./keys";
	private static String DIR_CERTS = "./certs";
	private static String SECRET = "HFJKEHF2-r-2dhUOI#_R0238r0-fwk3w=e2u8v f3h";
	
	// TODO: Private key should be encoded with SECRET.
	// TODO: getFname -> better naming?
	// TODO: How to generate good alias from user (email, name etc. is BAD, use SHA256 or something faster).
	// Same question for com.ib.Certificate (we use serial number which is just id.toString()).
	
	////////////////////////////////////////////////////////////
	// Filename generators
	////////////////////////////////////////////////////////////

	private String getFnameCert(String alias) {
		return DIR_CERTS + "/" + alias + ".cert";
	}
	
	private String getFnamePrivKey(String alias) {
		return DIR_KEYS + "/" + alias + ".key";
	}
	
	////////////////////////////////////////////////////////////
	// Certificate I/O
	////////////////////////////////////////////////////////////
	
	public void writeCert(X509Certificate certificate, String alias) {
		String fnameCert = getFnameCert(alias);
		
		try {
			PrintWriter out = new PrintWriter(fnameCert);
			out.write(certificate.toString());
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public X509Certificate readCert(String alias) {
		String fnameCert = getFnameCert(alias);
		
		try {
			CertificateFactory fac = CertificateFactory.getInstance("X509");
			FileInputStream is = new FileInputStream(fnameCert);
			X509Certificate cert = (X509Certificate) fac.generateCertificate(is);
			return cert;
		} catch (CertificateException | FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////
	// Key I/O
	////////////////////////////////////////////////////////////
	
	public void writePrivateKey(PrivateKey privateKey, User user) {
		String alias = user.getEmail();
		String fnamePriv = getFnamePrivKey(alias);
		
		try {	
			byte[] key2 = privateKey.getEncoded();
			FileOutputStream keyfos2 = new FileOutputStream(fnamePriv);
			keyfos2.write(Base64.getEncoder().encodeToString(key2).getBytes());
			keyfos2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fetch PrivateKey for given user.
	 * @param user The user. Does not have to be in the database.
	 * @return User's private key or null if not found.
	 */
	public PrivateKey readPrivateKey(User user) {
		String alias = user.getEmail();
		String fnamePriv = getFnamePrivKey(alias);
		try {
			if (!new File(fnamePriv).isFile()) {
				return null;
			}
			FileInputStream is = new FileInputStream(fnamePriv);
			byte[] key = Base64.getDecoder().decode(is.readAllBytes());
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
	        PrivateKey finalKey = keyFactory.generatePrivate(keySpec);
			
	        return finalKey;
		} 
		catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public PublicKey readPublicKeyFromStr(String publicKeyAsString) {
		try {
			byte[] byteKey = Base64.getDecoder().decode(publicKeyAsString.getBytes());
	        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        return kf.generatePublic(X509publicKey);	        
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String writePublicKeyToStr(PublicKey publicKey) {
		byte[] encodedBytes = Base64.getEncoder().encode(publicKey.getEncoded());
		return new String(encodedBytes);
	}
	
	////////////////////////////////////////////////////////////
	// Keygen
	////////////////////////////////////////////////////////////
	
	/**
	 * Fetch private and public key pairs for the given user.
	 * If not found (new user), new ones are generated.
	 * Does not write the private key to disk. Use <code>writePrivateKey</code> to do that.
	 * @param user The user. Does not have to be in the database.
	 * @return A pair of public and private keys.
	 */
	public KeyPair getOrCreateNewFor(User user) {
		PrivateKey privFromDisk = readPrivateKey(user);
		
		if (privFromDisk == null) {
			KeyPair kp = generateKeyPair(user);
			return kp;
		} else {
			return new KeyPair(readPublicKeyFromStr(user.getPublicKey()), privFromDisk);
		}
	}
	
	private KeyPair generateKeyPair(User user) {
		try {
			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keygen.initialize(2048, rand);
			return keygen.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}