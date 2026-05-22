package com.agriyield.escrowservice.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ESCROW_EXCHANGE = "escrow.exchange";

    public static final String ESCROW_LOCKED_KEY             = "escrow.locked";
    public static final String ESCROW_PARTIAL_RELEASED_KEY   = "escrow.partially.released";
    public static final String ESCROW_FULLY_RELEASED_KEY     = "escrow.fully.released";
    public static final String ESCROW_CANCELLED_KEY          = "escrow.cancelled";

    @Bean
    public TopicExchange escrowExchange() {
        return new TopicExchange(ESCROW_EXCHANGE, true, false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}