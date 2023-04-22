package com.ib;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ib.util.sendgrid.SendgridUtil;

@SpringBootApplication
public class SssscsApplication {
	public static void main(String[] args) {
		SpringApplication.run(SssscsApplication.class, args);
		
		SendgridUtil sendgridUtil = new SendgridUtil();
		sendgridUtil.sendEmail("YOU WIN", "SHDKj=shd==kjsdhs===kJHDKJSDHskjdhskj");
	}
}
