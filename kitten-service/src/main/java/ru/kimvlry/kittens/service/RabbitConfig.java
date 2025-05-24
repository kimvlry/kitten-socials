package ru.kimvlry.kittens.service;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String KITTENS_REQUEST_QUEUE = "kittens.request";

    @Bean
    public Queue kittenRequestQueue() {
        return new Queue(KITTENS_REQUEST_QUEUE, true);
    }
}