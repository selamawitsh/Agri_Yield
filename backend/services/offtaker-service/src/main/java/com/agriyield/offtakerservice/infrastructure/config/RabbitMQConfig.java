package com.agriyield.offtakerservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Exchanges ──────────────────────────────────────────────
    public static final String OFFTAKER_EXCHANGE    = "offtaker.exchange";
    public static final String GEOSPATIAL_EXCHANGE  = "geospatial.exchange";

    // ── Routing keys published ─────────────────────────────────
    public static final String BID_PLACED_KEY           = "bid.placed";
    public static final String BID_ACCEPTED_KEY         = "bid.accepted";
    public static final String HARVEST_CONFIRMED_KEY    = "harvest.confirmed";
    public static final String OFFTAKER_DEFAULTED_KEY   = "offtaker.defaulted";

    // ── Queues consumed ────────────────────────────────────────
    public static final String HARVEST_PREDICTED_QUEUE  = "offtaker.harvest-predicted.queue";
    public static final String YIELD_PREDICTED_QUEUE    = "offtaker.yield-predicted.queue";

    // ── Routing keys consumed ──────────────────────────────────
    private static final String HARVEST_PREDICTED_KEY   = "harvest.predicted";
    private static final String YIELD_PREDICTED_KEY     = "yield.predicted";

    @Bean
    public TopicExchange offtakerExchange() {
        return new TopicExchange(OFFTAKER_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange geospatialExchange() {
        return new TopicExchange(GEOSPATIAL_EXCHANGE, true, false);
    }

    @Bean
    public Queue harvestPredictedQueue() {
        return QueueBuilder.durable(HARVEST_PREDICTED_QUEUE).build();
    }

    @Bean
    public Queue yieldPredictedQueue() {
        return QueueBuilder.durable(YIELD_PREDICTED_QUEUE).build();
    }

    @Bean
    public Binding harvestPredictedBinding(Queue harvestPredictedQueue,
                                            TopicExchange geospatialExchange) {
        return BindingBuilder
                .bind(harvestPredictedQueue)
                .to(geospatialExchange)
                .with(HARVEST_PREDICTED_KEY);
    }

    @Bean
    public Binding yieldPredictedBinding(Queue yieldPredictedQueue,
                                          TopicExchange geospatialExchange) {
        return BindingBuilder
                .bind(yieldPredictedQueue)
                .to(geospatialExchange)
                .with(YIELD_PREDICTED_KEY);
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
