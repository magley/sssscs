package com.ib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SssscsApplication {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(SssscsApplication.class, args);
		try {
			Files.createDirectories(Paths.get("./keys"));
			Files.createDirectories(Paths.get("./certs"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// https://www.baeldung.com/java-restart-spring-boot-app#restart-by-creating-a-new-context
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
