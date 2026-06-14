package com.agriyield.farmservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // -------------------------------------------------------------------------
    // Farm exchange — published BY farm-service (SRS §5.2)
    // -------------------------------------------------------------------------
    public static final String FARM_EXCHANGE               = "farm.exchange";
    public static final String FARM_REGISTERED_KEY         = "farm.registered";
    public static final String INPUT_NEEDS_CREATED_KEY     = "input.needs.created";
    public static final String CROP_PHOTO_UPLOADED_KEY     = "crop.photo.uploaded";
    public static final String FARM_PLANTED_KEY            = "farm.planted";
    public static final String FARM_SATELLITE_VERIFIED_KEY = "farm.satellite.verified";

    // -------------------------------------------------------------------------
    // Investment exchange — consumed FROM investment-service
    // -------------------------------------------------------------------------
    public static final String INVESTMENT_EXCHANGE          = "investment.exchange";
    public static final String INVESTMENT_ESCROW_LOCKED_KEY = "investment.escrow.locked";
    public static final String INVESTMENT_CANCELLED_KEY     = "investment.cancelled";
    public static final String LISTING_FULLY_FUNDED_KEY     = "listing.fully.funded";
    public static final String LISTING_FUNDING_FAILED_KEY   = "listing.funding.failed";
    public static final String INVESTMENT_QUEUE             = "farm.investment-events.queue";

    // -------------------------------------------------------------------------
    // Offtaker exchange — consumed FROM offtaker-service (SRS §3.3.4 + §5.2)
    // farm-service listens for harvest.confirmed to trigger Agri-Score calculation
    // -------------------------------------------------------------------------
    public static final String OFFTAKER_EXCHANGE          = "offtaker.exchange";
    public static final String HARVEST_CONFIRMED_KEY      = "harvest.confirmed";
    public static final String HARVEST_EVENTS_QUEUE       = "farm.harvest-events.queue";

    // =========================================================================
    // Bean declarations
    // =========================================================================

    // --- Farm exchange (this service publishes to it) ------------------------
    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }

    // --- Investment exchange (this service consumes from it) -----------------
    @Bean
    public TopicExchange investmentExchange() {
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue investmentQueue() {
        return QueueBuilder.durable(INVESTMENT_QUEUE).build();
    }

    @Bean
    public Binding investmentBindingEscrow(Queue investmentQueue,
                                           TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue)
                .to(investmentExchange)
                .with(INVESTMENT_ESCROW_LOCKED_KEY);
    }

    @Bean
    public Binding investmentBindingCancelled(Queue investmentQueue,
                                              TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue)
                .to(investmentExchange)
                .with(INVESTMENT_CANCELLED_KEY);
    }

    @Bean
    public Binding listingBindingFullyFunded(Queue investmentQueue,
                                             TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue)
                .to(investmentExchange)
                .with(LISTING_FULLY_FUNDED_KEY);
    }

    @Bean
    public Binding listingBindingFundingFailed(Queue investmentQueue,
                                               TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentQueue)
                .to(investmentExchange)
                .with(LISTING_FUNDING_FAILED_KEY);
    }

    // --- Offtaker exchange (this service consumes from it) -------------------
    @Bean
    public TopicExchange offtakerExchange() {
        return new TopicExchange(OFFTAKER_EXCHANGE, true, false);
    }

    @Bean
    public Queue harvestEventsQueue() {
        return QueueBuilder.durable(HARVEST_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding harvestConfirmedBinding(Queue harvestEventsQueue,
                                           TopicExchange offtakerExchange) {
        return BindingBuilder.bind(harvestEventsQueue)
                .to(offtakerExchange)
                .with(HARVEST_CONFIRMED_KEY);
    }

    // --- Shared infrastructure -----------------------------------------------
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