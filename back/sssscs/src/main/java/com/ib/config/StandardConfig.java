package com.ib.config;

import java.security.Security;
import java.sql.SQLException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.h2.tools.Server;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ib.pki.KeyUtil;
import com.ib.util.recaptcha.ReCAPTCHAUtil;
import com.ib.util.twilio.SendgridUtil;

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

	@Bean
	public KeyUtil keyUtil() {
		Security.addProvider(new BouncyCastleProvider());
		return new KeyUtil();
	}

	@Bean
	public SendgridUtil sendgridUtil() {
		return new SendgridUtil();
	}

	@Bean
	public ReCAPTCHAUtil reCAPTCHAUtil() {
		return new ReCAPTCHAUtil();
	}
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server inMemoryH2DatabaseaServer() throws SQLException {
		// open to others: to have a reason to use SSL
		return Server.createWebServer(
	      "-webAllowOthers", "-webPort", "9090", "-webSSL");
	}

}
