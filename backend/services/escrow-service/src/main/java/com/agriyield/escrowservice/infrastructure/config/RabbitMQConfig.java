package com.agriyield.escrowservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Exchanges this service publishes to ───────────────────────────────────
    public static final String ESCROW_EXCHANGE             = "escrow.exchange";
    public static final String ESCROW_LOCKED_KEY           = "escrow.locked";
    public static final String ESCROW_PARTIAL_RELEASED_KEY = "escrow.partially.released";
    public static final String ESCROW_FULLY_RELEASED_KEY   = "escrow.fully.released";
    public static final String ESCROW_CANCELLED_KEY        = "escrow.cancelled";

    // ── Weather exchange this service consumes ────────────────────────────────
    // SRS §5.2: drought.triggered → escrow-service processes parametric refund
    public static final String WEATHER_EXCHANGE            = "weather.exchange";
    public static final String DROUGHT_QUEUE               = "escrow.drought-triggered.queue";
    public static final String DROUGHT_ROUTING_KEY         = "drought.triggered";

    // ── Exchange beans ────────────────────────────────────────────────────────
    @Bean
    public TopicExchange escrowExchange() {
        return new TopicExchange(ESCROW_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange weatherExchange() {
        return new TopicExchange(WEATHER_EXCHANGE, true, false);
    }

    // ── Queue + binding for drought.triggered ─────────────────────────────────
    @Bean
    public Queue droughtTriggeredQueue() {
        return QueueBuilder.durable(DROUGHT_QUEUE).build();
    }

    @Bean
    public Binding droughtTriggeredBinding(Queue droughtTriggeredQueue,
                                           TopicExchange weatherExchange) {
        return BindingBuilder
                .bind(droughtTriggeredQueue)
                .to(weatherExchange)
                .with(DROUGHT_ROUTING_KEY);
    }

    // ── Message converter ─────────────────────────────────────────────────────
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