package com.library.book_storage_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    @Bean
    public NewTopic bookTopic() {
        return new NewTopic("books", 1, (short) 1);
    }
}
