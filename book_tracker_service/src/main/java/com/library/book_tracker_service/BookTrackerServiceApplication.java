package com.library.book_tracker_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BookTrackerServiceApplication {
	private static final Logger log = LoggerFactory.getLogger(BookTrackerServiceApplication.class);
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(BookTrackerServiceApplication.class, args);
		log.info("Server started at http://localhost:"+ context.getEnvironment().getProperty("server.port"));
	}

}
