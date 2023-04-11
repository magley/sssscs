package com.ib.pki;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;

public class KeyStoreUtil {
	private String secret = "MySecret47389473289";
	private String filename = "./pki/keystore.jks";
	
	@Autowired
	private KeyStore ks;
	
	public void setPrivateKey(String alias, PrivateKey privateKey) {
		try {
			Certificate rootCertificate = ks.getCertificate("1"); // TODO: Hardcoded alias for root certificate is BAD.
			ks.setKeyEntry(alias, privateKey, secret.toCharArray(), new Certificate[] {rootCertificate});
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
}
