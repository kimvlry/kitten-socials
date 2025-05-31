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
    public TopicExchange kittenExchange() {
        return new TopicExchange("kitten.exchange");
    }

    @Bean
    public Queue createQueue() {
        return new Queue("kitten.create.queue");
    }

    @Bean
    public Queue updateQueue() {
        return new Queue("kitten.update.queue");
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue("kitten.delete.queue");
    }
    @Bean
    public Binding createBinding(Queue createQueue, TopicExchange kittenExchange) {
        return BindingBuilder.bind(createQueue).to(kittenExchange).with("kitten.create");
    }

    @Bean
    public Binding updateBinding(Queue updateQueue, TopicExchange kittenExchange) {
        return BindingBuilder.bind(updateQueue).to(kittenExchange).with("kitten.update");
    }

    @Bean
    public Binding deleteBinding(Queue deleteQueue, TopicExchange kittenExchange) {
        return BindingBuilder.bind(deleteQueue).to(kittenExchange).with("kitten.delete");
    }


    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory factory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> {
            builder.modules(new JavaTimeModule());
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        };
    }
}
