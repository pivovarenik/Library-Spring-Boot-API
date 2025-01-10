package com.library.book_storage_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BookStorageServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(BookStorageServiceApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BookStorageServiceApplication.class, args);
		log.info("Server started at http://localhost:"+ context.getEnvironment().getProperty("server.port"));
	}
	@GetMapping("/")
	public String home() {
		return "home";
	}
}
