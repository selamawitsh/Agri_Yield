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

    // Investment exchange (consumed from investment-service)
    public static final String INVESTMENT_EXCHANGE = "investment.exchange";
    public static final String INVESTMENT_ESCROW_LOCKED_KEY = "investment.escrow.locked";
    public static final String INVESTMENT_CANCELLED_KEY = "investment.cancelled";
    public static final String LISTING_FULLY_FUNDED_KEY = "listing.fully.funded";
    public static final String LISTING_FUNDING_FAILED_KEY = "listing.funding.failed";
    public static final String INVESTMENT_QUEUE = "farm.investment-events.queue";

    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange investmentExchange() {
        // Reference investment.exchange so this service can bind a queue to it
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue investmentQueue() {
        return QueueBuilder.durable(INVESTMENT_QUEUE).build();
    }

    @Bean
    public Binding investmentBindingEscrow(Queue investmentQueue, TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue).to(investmentExchange).with(INVESTMENT_ESCROW_LOCKED_KEY);
    }

    @Bean
    public Binding investmentBindingCancelled(Queue investmentQueue, TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue).to(investmentExchange).with(INVESTMENT_CANCELLED_KEY);
    }

    @Bean
    public Binding listingBindingFullyFunded(Queue investmentQueue, TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue).to(investmentExchange).with(LISTING_FULLY_FUNDED_KEY);
    }

    @Bean
    public Binding listingBindingFundingFailed(Queue investmentQueue, TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue).to(investmentExchange).with(LISTING_FUNDING_FAILED_KEY);
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
