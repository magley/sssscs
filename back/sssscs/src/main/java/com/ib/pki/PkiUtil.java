package com.ib.pki;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.ib.certificate.Certificate;
import com.ib.pki.data.IssuerData;
import com.ib.pki.data.SubjectData;
import com.ib.user.User;

public class PkiUtil {
	/*
	private String secret = "MySecret47389473289";
	private String filename = "./pki/keystore.jks";
	private KeyStore ks;
	
	public PkiUtil() {
		Security.addProvider(new BouncyCastleProvider());
		try {
			ks = KeyStore.getInstance("JKS");
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
	}
	
	public X509Certificate createCert(IssuerData issuer, SubjectData subject) {
		
	}
	
	public X509Certificate createRootCert() {
		
	}
	
	public void saveKeyPair(KeyPair keyPair, String alias, Certificate root) {
		IssuerData issuer = issuerFrom(root.getIssuer());
		
	}
	
	public IssuerData issuerFrom(User user) {
		X500Name name = getx500NameFrom(user);
		PrivateKey privateKey = getPrivateKey(user.getEmail());
		return new IssuerData(privateKey, name);
	}
	
	public SubjectData subjectFrom(User user) {
		X500Name name = getx500NameFrom(user);
		String certificateSerialNumber = user.getEmail() + " Key Cert";
		// TODO: Finish.
		
	}
	
	public X500Name getx500NameFrom(User user) {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.CN, user.getName() + " " + user.getSurname());
		builder.addRDN(BCStyle.NAME, user.getName());
		builder.addRDN(BCStyle.SURNAME, user.getSurname());
		builder.addRDN(BCStyle.E, user.getEmail());
		builder.addRDN(BCStyle.UID, user.getEmail().toString());
		return builder.build();
	}
	
	public PrivateKey getPrivateKey(String alias) {
		try {
			if (ks.isKeyEntry(alias)) {
				return (PrivateKey) ks.getKey(alias, secret.toCharArray());
			}
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
	*/
}
