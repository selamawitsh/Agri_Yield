package com.agriyield.investmentservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Exchanges ─────────────────────────────────────────────────────────────
    public static final String INVESTMENT_EXCHANGE = "investment.exchange";
    public static final String FARM_EXCHANGE       = "farm.exchange";
    public static final String OFFTAKER_EXCHANGE   = "offtaker.exchange";

    // ── Routing keys published ────────────────────────────────────────────────
    public static final String INVESTMENT_PLACED_KEY        = "investment.placed";
    public static final String INVESTMENT_ESCROW_LOCKED_KEY = "investment.escrow.locked";
    public static final String INVESTMENT_CANCELLED_KEY     = "investment.cancelled";
    public static final String INVESTMENT_COMPLETED_KEY     = "investment.completed";
    public static final String LISTING_CREATED_KEY          = "listing.created";
    // FIX: was "listing.fully.funded" — SRS §2.2.3 says voucher-service binds to
    // investment.exchange with routing key "investment.funded". Aligning here so
    // voucher-service receives the event and generates vouchers after full funding.
    public static final String LISTING_FULLY_FUNDED_KEY     = "investment.funded";
    public static final String LISTING_FUNDING_FAILED_KEY   = "listing.funding.failed";

    // ── Queues consumed ───────────────────────────────────────────────────────
    public static final String INPUT_NEEDS_QUEUE            = "investment.input-needs.queue";
    public static final String INPUT_NEEDS_ROUTING_KEY      = "input.needs.created";
    public static final String BID_ACCEPTED_QUEUE           = "investment.bid-accepted.queue";
    public static final String SETTLEMENT_COMPLETED_QUEUE   = "investment.settlement-completed.queue";

    // ── Exchange beans ────────────────────────────────────────────────────────
    @Bean
    public TopicExchange investmentExchange() {
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange offtakerExchange() {
        return new TopicExchange(OFFTAKER_EXCHANGE, true, false);
    }

    // ── Queue beans ───────────────────────────────────────────────────────────
    @Bean
    public Queue inputNeedsQueue() {
        return QueueBuilder.durable(INPUT_NEEDS_QUEUE).build();
    }

    @Bean
    public Queue bidAcceptedQueue() {
        return QueueBuilder.durable(BID_ACCEPTED_QUEUE).build();
    }

    @Bean
    public Queue settlementCompletedQueue() {
        return QueueBuilder.durable(SETTLEMENT_COMPLETED_QUEUE).build();
    }

    // ── Binding beans ─────────────────────────────────────────────────────────
    @Bean
    public Binding inputNeedsBinding(Queue inputNeedsQueue, TopicExchange farmExchange) {
        return BindingBuilder
                .bind(inputNeedsQueue)
                .to(farmExchange)
                .with(INPUT_NEEDS_ROUTING_KEY);
    }

    // ── Serialization ─────────────────────────────────────────────────────────
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