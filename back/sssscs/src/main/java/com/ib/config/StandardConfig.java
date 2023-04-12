package com.ib.config;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ib.pki.manual.KeyUtil;

@Configuration
public class StandardConfig {
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

//	@Bean
//	public KeyPairUtil keyPairUtil() {
//		return new KeyPairUtil();
//	}
//	
//	@Bean
//	public KeyStoreUtil keyStoreUtil() {
//		KeyStoreUtil ksUtil = new KeyStoreUtil();
//		ksUtil.loadKeyStore();
//		return ksUtil;
//	}
	
	@Bean
	public KeyUtil keyUtil() {
		Security.addProvider(new BouncyCastleProvider());
		return new KeyUtil();
	}
}
