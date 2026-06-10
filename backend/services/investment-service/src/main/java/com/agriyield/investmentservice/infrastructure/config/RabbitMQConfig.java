package com.agriyield.investmentservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INVESTMENT_EXCHANGE          = "investment.exchange";
    public static final String INVESTMENT_PLACED_KEY        = "investment.placed";
    public static final String INVESTMENT_ESCROW_LOCKED_KEY = "investment.escrow.locked";
    public static final String INVESTMENT_CANCELLED_KEY     = "investment.cancelled";
    public static final String INVESTMENT_COMPLETED_KEY     = "investment.completed";
    public static final String LISTING_CREATED_KEY          = "listing.created";
    public static final String LISTING_FULLY_FUNDED_KEY     = "listing.fully.funded";
    public static final String LISTING_FUNDING_FAILED_KEY   = "listing.funding.failed";

    public static final String FARM_EXCHANGE                = "farm.exchange";
    public static final String INPUT_NEEDS_QUEUE            = "investment.input-needs.queue";
    public static final String INPUT_NEEDS_ROUTING_KEY      = "input.needs.created";

    @Bean
    public TopicExchange investmentExchange() {
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }

    @Bean
    public Queue inputNeedsQueue() {
        return QueueBuilder.durable(INPUT_NEEDS_QUEUE).build();
    }

    @Bean
    public Binding inputNeedsBinding(Queue inputNeedsQueue,
                                     TopicExchange farmExchange) {
        return BindingBuilder
            .bind(inputNeedsQueue)
            .to(farmExchange)
            .with(INPUT_NEEDS_ROUTING_KEY);
    }

    @Bean
    public TopicExchange offtakerExchange() {
        return new TopicExchange("offtaker.exchange", true, false);
    }

    @Bean
    public Queue bidAcceptedQueue() {
        return QueueBuilder.durable("investment.bid-accepted.queue").build();
    }

    @Bean
    public Queue settlementCompletedQueue() {
        return QueueBuilder.durable("investment.settlement-completed.queue").build();
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
// NOTE: The above is appended — merge these beans into RabbitMQConfig class manually.
// Add to RabbitMQConfig:
//
//    public static final String OFFTAKER_EXCHANGE          = "offtaker.exchange";
//    public static final String BID_ACCEPTED_QUEUE         = "investment.bid-accepted.queue";
//    public static final String SETTLEMENT_COMPLETED_QUEUE = "investment.settlement-completed.queue";
//
//    @Bean public TopicExchange offtakerExchange() { return new TopicExchange(OFFTAKER_EXCHANGE, true, false); }
//    @Bean public Queue bidAcceptedQueue()          { return QueueBuilder.durable(BID_ACCEPTED_QUEUE).build(); }
//    @Bean public Queue settlementCompletedQueue()  { return QueueBuilder.durable(SETTLEMENT_COMPLETED_QUEUE).build(); }
