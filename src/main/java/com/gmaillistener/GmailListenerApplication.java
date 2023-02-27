package com.gmaillistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GmailListenerApplication {

	private static final String SERVICE_ACCOUNT_KEY_PATH = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

	public static void main(String[] args) {
		SpringApplication.run(GmailListenerApplication.class, args);

	}

}
