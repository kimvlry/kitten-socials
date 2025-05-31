package ru.kimvlry.kittens.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RabbitConfig {
    @Bean
    public TopicExchange ownerExchange() {
        return new TopicExchange("owner.exchange");
    }

    @Bean
    public Queue createQueue() {
        return new Queue("owner.create.queue");
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("owner.update.queue");
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue("owner.delete.queue");
    }

    @Bean
    public Queue createForUserQueue() {
        return new Queue("owner.create-for-user.queue");
    }

    @Bean
    public Binding createBinding(Queue createQueue, TopicExchange ownerExchange) {
        return BindingBuilder.bind(createQueue).to(ownerExchange).with("owner.create");
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, TopicExchange ownerExchange) {
        return BindingBuilder.bind(updateQueue).to(ownerExchange).with("owner.update");
    }

    @Bean
    public Binding deleteBinding(Queue deleteQueue, TopicExchange ownerExchange) {
        return BindingBuilder.bind(deleteQueue).to(ownerExchange).with("owner.delete");
    }

    @Bean
    public Binding createForUserBinding(Queue createForUserQueue, TopicExchange ownerExchange) {
        return BindingBuilder.bind(createForUserQueue).to(ownerExchange).with("owner.create-for-user");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory factory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter converter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
