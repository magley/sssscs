package com.ib.pki;

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

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.openssl.jcajce.JcaMiscPEMGenerator;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

import com.ib.user.User;

public class KeyUtil {
    private static String DIR_KEYS = "./keys";
    private static String DIR_CERTS = "./certs";
 
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
    // Read and write keys/certs.
    ////////////////////////////////////////////////////////////
    
    public PrivateKey getPrivateKey(String alias) {
        String fnamePriv = getFnamePrivKey(alias);
        if (!new File(fnamePriv).isFile()) {
            return null;
        }
        
        try (FileInputStream is = new FileInputStream(fnamePriv)) {
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
        
        try (FileInputStream is = new FileInputStream(fnameCert)) {
            CertificateFactory fac = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) fac.generateCertificate(is);
            return cert;
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void savePrivateKey(String alias, PrivateKey privateKey) {
        String fnamePriv = getFnamePrivKey(alias);
        
        try (FileOutputStream keyfos = new FileOutputStream(fnamePriv)) {	
            byte[] key = privateKey.getEncoded();
            keyfos.write(Base64.getEncoder().encodeToString(key).getBytes());
            keyfos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveX509Certificate(String alias, X509Certificate certificate) {
        String fnameCert = getFnameCert(alias);
        
        try (PrintWriter out = new PrintWriter(fnameCert)){
            JcaPEMWriter pemWriter = new JcaPEMWriter(out);
            PemObjectGenerator objGen = new JcaMiscPEMGenerator(certificate);
            pemWriter.writeObject(objGen);
            pemWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    ////////////////////////////////////////////////////////////
    // Key-gen
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
    
    ////////////////////////////////////////////////////////////
    // User data gen
    ////////////////////////////////////////////////////////////
    
    public X500Name getX500Name(User user) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, user.getUsername());
        builder.addRDN(BCStyle.NAME, user.getName());
        builder.addRDN(BCStyle.SURNAME, user.getSurname());
        builder.addRDN(BCStyle.E, user.getEmail());
        builder.addRDN(BCStyle.TELEPHONE_NUMBER, user.getPhoneNumber());
        builder.addRDN(BCStyle.UID, user.getId().toString());
        return builder.build();
    }
}
