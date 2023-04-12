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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;

public class KeyUtil {
	private static String DIR_KEYS = "./keys";
	private static String DIR_CERTS = "./certs";
	private static String SECRET = "HFJKEHF2-r-2dhUOI#_R0238r0-fwk3w=e2u8v f3h";
	
	////////////////////////////////////////////////////////////
	// Filename generators
	////////////////////////////////////////////////////////////

	private String getFnameCert(String alias) {
		return DIR_CERTS + "/" + alias + ".crt";
	}
	
	private String getFnamePrivKey(String alias) {
		return DIR_KEYS + "/" + alias + ".key";
	}
	
	////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////
	
	public PrivateKey getPrivateKey(String alias) {
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
	
	public PublicKey getPublicKey(String alias) {
		X509Certificate cert = getX509Certificate(alias);
		return cert.getPublicKey();
	}
	
	public X509Certificate getX509Certificate(String alias) {
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
	
	public void savePrivateKey(String alias, PrivateKey privateKey) {
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
	
	public void saveX509Certificate(String alias, X509Certificate certificate) {
		String fnameCert = getFnameCert(alias);
		
		try {
			PrintWriter out = new PrintWriter(fnameCert);
			out.write(certificate.toString());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////
	// Keygen
	////////////////////////////////////////////////////////////
	
	public KeyPair generateKeyPair() {
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
	
	public Date dateFrom(LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}
	
	public X500Name getX500Name(String name) {
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		builder.addRDN(BCStyle.NAME, name);
		//builder.addRDN(BCStyle.SURNAME, user.getSurname());
		//builder.addRDN(BCStyle.E, user.getEmail());
		//builder.addRDN(BCStyle.TELEPHONE_NUMBER, user.getPhoneNumber());
		//builder.addRDN(BCStyle.UID, user.getId().toString());
		return builder.build();
	}
	
	/**
	   * Creates the hash value of the public key.
	   *
	   * @param publicKey of the certificate
	   *
	   * @return SubjectKeyIdentifier hash
	   *
	   * @throws OperatorCreationException
	   */
	  public static SubjectKeyIdentifier createSubjectKeyId(final PublicKey publicKey) throws OperatorCreationException {
		  final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		  final DigestCalculator digCalc = new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
		  return new X509ExtensionUtils(digCalc).createSubjectKeyIdentifier(publicKeyInfo);
	  }

	  /**
	   * Creates the hash value of the authority public key.
	   *
	   * @param publicKey of the authority certificate
	   *
	   * @return AuthorityKeyIdentifier hash
	   *
	   * @throws OperatorCreationException
	   */
	  public static AuthorityKeyIdentifier createAuthorityKeyId(final PublicKey publicKey) throws OperatorCreationException {
		  final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		  final DigestCalculator digCalc =
	      new BcDigestCalculatorProvider().get(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1));
		  return new X509ExtensionUtils(digCalc).createAuthorityKeyIdentifier(publicKeyInfo);
	  }
	
	
	
	
	////////////////////////////////////////////////////////////
	// User data gen
	////////////////////////////////////////////////////////////
	
//	public SubjectData getSubject(String name, String surname, String email, Certificate cert) {
//	KeyPair keyPair = generateKeyPair();
//	writePrivateKey(keyPair.getPrivate(), null);
//	PublicKey key = generateKeyPair().getPublic(); // TODO ???
//	
//	X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
//	builder.addRDN(BCStyle.NAME, name);
//	builder.addRDN(BCStyle.SURNAME, surname);
//	builder.addRDN(BCStyle.E, email);
//	X500Name x500Name = builder.build();
//	
//	Date from = Date.from(cert.getValidFrom().atZone(ZoneId.systemDefault()).toInstant());
//	Date to = Date.from(cert.getValidTo().atZone(ZoneId.systemDefault()).toInstant());
//	
//	return new SubjectData(key, x500Name, cert.getSerialNumber(), from, to);
//}
	

	
//	public IssuerData getIssuer(User user) {
//		PrivateKey key = readPrivateKey(user);
//		X500Name name = getX500Name(user);
//		return new IssuerData(key, name);
//	}
//	

//	
//	
//	// TODO: Private key should be encoded with SECRET.
//	// TODO: getFname -> better naming?
//	// TODO: How to generate good alias from user (email, name etc. is BAD, use SHA256 or something faster).
//	// Same question for com.ib.Certificate (we use serial number which is just id.toString()).
//	// TODO: PEM Compliance for storing keys and certificates. https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail
//	
//	////////////////////////////////////////////////////////////
//	// User to Issuer/Subject
//	////////////////////////////////////////////////////////////
//	
//	
//	public IssuerData getIssuer(User user) {
//		PrivateKey key = readPrivateKey(user);
//		X500Name name = getX500Name(user);
//		return new IssuerData(key, name);
//	}
//	
//	public SubjectData getSubject(String name, String surname, String email, Certificate cert) {
//		KeyPair keyPair = generateKeyPair();
//		writePrivateKey(keyPair.getPrivate(), null);
//		PublicKey key = generateKeyPair().getPublic(); // TODO ???
//		
//		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
//		builder.addRDN(BCStyle.NAME, name);
//		builder.addRDN(BCStyle.SURNAME, surname);
//		builder.addRDN(BCStyle.E, email);
//		X500Name x500Name = builder.build();
//		
//		Date from = Date.from(cert.getValidFrom().atZone(ZoneId.systemDefault()).toInstant());
//		Date to = Date.from(cert.getValidTo().atZone(ZoneId.systemDefault()).toInstant());
//		
//		return new SubjectData(key, x500Name, cert.getSerialNumber(), from, to);
//	}
//	
//	private X500Name getX500Name(User user) {
//		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
//		builder.addRDN(BCStyle.NAME, user.getName());
//		builder.addRDN(BCStyle.SURNAME, user.getSurname());
//		builder.addRDN(BCStyle.E, user.getEmail());
//		builder.addRDN(BCStyle.TELEPHONE_NUMBER, user.getPhoneNumber());
//		builder.addRDN(BCStyle.UID, user.getId().toString());
//		return builder.build();
//	}
//
//	////////////////////////////////////////////////////////////
//	// Filename generators
//	////////////////////////////////////////////////////////////
//
//	private String getFnameCert(String alias) {
//		return DIR_CERTS + "/" + alias + ".cert";
//	}
//	
//	private String getFnamePrivKey(String alias) {
//		return DIR_KEYS + "/" + alias + ".key";
//	}
//	
//	////////////////////////////////////////////////////////////
//	// Certificate I/O
//	////////////////////////////////////////////////////////////
//	
//	public void writeCert(X509Certificate certificate, String alias) {
//		String fnameCert = getFnameCert(alias);
//		
//		try {
//			PrintWriter out = new PrintWriter(fnameCert);
//			out.write(certificate.toString());
//			out.flush();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public X509Certificate readCert(String alias) {
//		String fnameCert = getFnameCert(alias);
//		
//		try {
//			CertificateFactory fac = CertificateFactory.getInstance("X509");
//			FileInputStream is = new FileInputStream(fnameCert);
//			X509Certificate cert = (X509Certificate) fac.generateCertificate(is);
//			return cert;
//		} catch (CertificateException | FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	////////////////////////////////////////////////////////////
//	// Key I/O
//	////////////////////////////////////////////////////////////
//	
//	public void writePrivateKey(PrivateKey privateKey, User user) {
//		String alias = user.getEmail();
//		String fnamePriv = getFnamePrivKey(alias);
//		
//		try {	
//			byte[] key2 = privateKey.getEncoded();
//			FileOutputStream keyfos2 = new FileOutputStream(fnamePriv);
//			keyfos2.write(Base64.getEncoder().encodeToString(key2).getBytes());
//			keyfos2.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	/**
//	 * Fetch PrivateKey for given user.
//	 * @param user The user. Does not have to be in the database.
//	 * @return User's private key or null if not found.
//	 */
//	public PrivateKey readPrivateKey(String alias) {
//		String fnamePriv = getFnamePrivKey(alias);
//		try {
//			if (!new File(fnamePriv).isFile()) {
//				return null;
//			}
//			FileInputStream is = new FileInputStream(fnamePriv);
//			byte[] key = Base64.getDecoder().decode(is.readAllBytes());
//	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//	        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
//	        PrivateKey finalKey = keyFactory.generatePrivate(keySpec);
//			
//	        return finalKey;
//		} 
//		catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	public PublicKey readPublicKeyFromStr(String publicKeyAsString) {
//		try {
//			byte[] byteKey = Base64.getDecoder().decode(publicKeyAsString.getBytes());
//	        X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(byteKey);
//	        KeyFactory kf = KeyFactory.getInstance("RSA");
//	        return kf.generatePublic(X509publicKey);	        
//		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	public String writePublicKeyToStr(PublicKey publicKey) {
//		byte[] encodedBytes = Base64.getEncoder().encode(publicKey.getEncoded());
//		return new String(encodedBytes);
//	}
//	
//	////////////////////////////////////////////////////////////
//	// Keygen
//	////////////////////////////////////////////////////////////
//	
//	/**
//	 * Fetch private and public key pairs for the given alias.
//	 * If not found, new ones are generated.
//	 * Does not write the private key to disk. Use <code>writePrivateKey</code> to do that.
//	 * @return A pair of public and private keys.
//	 */
//	public KeyPair getOrCreateNewFor(String alias) {
//		PrivateKey privFromDisk = readPrivateKey(alias);
//		
//		if (privFromDisk == null) {
//			KeyPair kp = generateKeyPair();
//			return kp;
//		} else {
//			return new KeyPair(readPublicKeyFromStr(alias), privFromDisk);
//		}
//	}
//	
//	private KeyPair generateKeyPair() {
//		try {
//			KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
//			SecureRandom rand = SecureRandom.getInstance("SHA1PRNG", "SUN");
//			keygen.initialize(2048, rand);
//			return keygen.generateKeyPair();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
}
