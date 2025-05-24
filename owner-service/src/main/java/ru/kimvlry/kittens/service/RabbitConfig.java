package ru.kimvlry.kittens.service;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String OWNERS_REQUEST_QUEUE = "owners.request";

    @Bean
    public Queue ownersRequestQueue() {
        return new Queue(OWNERS_REQUEST_QUEUE, true);
    }
}