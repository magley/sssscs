package com.ib;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ib.util.twilio.SendgridUtil;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SssscsApplication {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(SssscsApplication.class, args);
	}

	//https://www.baeldung.com/java-restart-spring-boot-app#restart-by-creating-a-new-context
	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			context.close();
			context = SpringApplication.run(SssscsApplication.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}
}
