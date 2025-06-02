package com.antonkuzmichev.blackjack_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlackjackServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlackjackServerApplication.class, args);
		System.out.println("Blackjack Server started on http://localhost:8080");
		System.out.println("WebSocket endpoint: ws://localhost:8080/game");
	}

}
