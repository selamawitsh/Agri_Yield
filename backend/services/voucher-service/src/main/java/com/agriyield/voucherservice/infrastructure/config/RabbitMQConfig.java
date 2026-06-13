package com.agriyield.voucherservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Voucher exchange
    public static final String VOUCHER_EXCHANGE           = "voucher.exchange";
    public static final String VOUCHER_GENERATED_KEY      = "voucher.generated";
    public static final String VOUCHER_REDEEMED_KEY       = "voucher.redeemed";
    public static final String VOUCHER_EXPIRED_KEY        = "voucher.expired";
    public static final String VOUCHER_CANCELLED_KEY      = "voucher.cancelled";
    public static final String VOUCHER_REJECTED_KEY       = "voucher.rejected";

    // Investment exchange
    public static final String INVESTMENT_EXCHANGE        = "investment.exchange";
    public static final String LISTING_FUNDED_QUEUE       = "voucher.listing-funded.queue";
    public static final String LISTING_FUNDED_KEY         = "listing.fully.funded";
    public static final String INVESTMENT_CANCELLED_QUEUE = "voucher.investment-cancelled.queue";
    public static final String INVESTMENT_CANCELLED_KEY   = "investment.cancelled";

    // Farm exchange — consume input.needs.created to cache product details
    public static final String FARM_EXCHANGE              = "farm.exchange";
    public static final String INPUT_NEEDS_QUEUE          = "voucher.input-needs.queue";
    public static final String INPUT_NEEDS_KEY            = "input.needs.created";

    @Bean public TopicExchange voucherExchange() {
        return new TopicExchange(VOUCHER_EXCHANGE, true, false);
    }
    @Bean public TopicExchange investmentExchange() {
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
    }
    @Bean public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }
    @Bean public Queue listingFundedQueue() {
        return QueueBuilder.durable(LISTING_FUNDED_QUEUE).build();
    }
    @Bean public Queue investmentCancelledQueue() {
        return QueueBuilder.durable(INVESTMENT_CANCELLED_QUEUE).build();
    }
    @Bean public Queue inputNeedsQueue() {
        return QueueBuilder.durable(INPUT_NEEDS_QUEUE).build();
    }
    @Bean public Binding listingFundedBinding(Queue listingFundedQueue,
                                              TopicExchange investmentExchange) {
        return BindingBuilder.bind(listingFundedQueue)
            .to(investmentExchange).with(LISTING_FUNDED_KEY);
    }
    @Bean public Binding investmentCancelledBinding(Queue investmentCancelledQueue,
                                                    TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentCancelledQueue)
            .to(investmentExchange).with(INVESTMENT_CANCELLED_KEY);
    }
    @Bean public Binding inputNeedsBinding(Queue inputNeedsQueue,
                                           TopicExchange farmExchange) {
        return BindingBuilder.bind(inputNeedsQueue)
            .to(farmExchange).with(INPUT_NEEDS_KEY);
    }
    @Bean public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    @Bean public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
