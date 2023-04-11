package com.ib.pki;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Date;

import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import com.ib.user.User;

public class KeyStoreUtil {
	private String secret = "MySecret47389473289";
	private String filename = "./pki/keystore.jks";
	private KeyStore ks;
	
	public KeyStoreUtil() {
		Security.addProvider(new BouncyCastleProvider());
		try {
			ks = KeyStore.getInstance("JKS");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
	
	public KeyStore getKs() {
		return ks;
	}
	
	public PublicKey getPublicKey(String key) {
		try{
	        byte[] byteKey = Base64.getDecoder().decode(key);
	        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
	        KeyFactory kf = KeyFactory.getInstance("RSA");
	        return kf.generatePublic(X509publicKey);
	    }
	    catch(Exception e){
	        e.printStackTrace();
	    }

	    return null;
	}
	
	public void setPrivateKey(String alias, PrivateKey privateKey) {
		try {
			Certificate rootCertificate = ks.getCertificate("1"); // TODO: Hardcoded alias for root certificate is BAD.
			// TODO: We need to create a (self-signed?) certificate of this private key that stores the public key.
			ks.setKeyEntry(alias, privateKey, secret.toCharArray(), new Certificate[] {rootCertificate});
			
			saveKeyStore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public PrivateKey getPrivateKeyOrNull(String alias) {
		try {
			if (ks.isKeyEntry(alias)) {
				return (PrivateKey) ks.getKey(alias, secret.toCharArray());
			}
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void createEmpty() {
		try {
			ks.load(null, secret.toCharArray());
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadKeyStore() {
        try {
        	try {
        		ks.load(new FileInputStream(filename), secret.toCharArray());
        	} catch (FileNotFoundException ex) {
            	ks.load(null, secret.toCharArray());
            }
        } catch (NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        } 
    }

    public void saveKeyStore() {
        try {
            ks.store(new FileOutputStream(filename), secret.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
    }
    
    public X509Certificate generateRootInMemory() {
		return null;
    }
}
