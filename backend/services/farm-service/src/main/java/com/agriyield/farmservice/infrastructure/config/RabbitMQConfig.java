package com.agriyield.farmservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange
    public static final String FARM_EXCHANGE = "farm.exchange";

    // Routing keys published by farm-service (SRS Section 5.2)
    public static final String FARM_REGISTERED_KEY      = "farm.registered";
    public static final String INPUT_NEEDS_CREATED_KEY  = "input.needs.created";
    public static final String CROP_PHOTO_UPLOADED_KEY  = "crop.photo.uploaded";
    public static final String FARM_PLANTED_KEY         = "farm.planted";
    public static final String FARM_SATELLITE_VERIFIED_KEY = "farm.satellite.verified";

    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
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
