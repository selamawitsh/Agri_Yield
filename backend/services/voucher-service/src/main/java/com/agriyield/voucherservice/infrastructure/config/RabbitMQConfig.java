package com.agriyield.voucherservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange this service publishes to
    public static final String VOUCHER_EXCHANGE              = "voucher.exchange";
    public static final String VOUCHER_GENERATED_KEY         = "voucher.generated";
    public static final String VOUCHER_REDEEMED_KEY          = "voucher.redeemed";
    public static final String VOUCHER_EXPIRED_KEY           = "voucher.expired";
    public static final String VOUCHER_CANCELLED_KEY         = "voucher.cancelled";

    // Investment exchange this service listens to (IS-06: listing fully funded)
    public static final String INVESTMENT_EXCHANGE           = "investment.exchange";
    public static final String LISTING_FULLY_FUNDED_QUEUE   = "voucher.listing-funded.queue";
    public static final String LISTING_FULLY_FUNDED_KEY     = "listing.fully.funded";

    // Investment cancelled — cancel all its vouchers
    public static final String INVESTMENT_CANCELLED_QUEUE   = "voucher.investment-cancelled.queue";
    public static final String INVESTMENT_CANCELLED_KEY     = "investment.cancelled";

    @Bean
    public TopicExchange voucherExchange() {
        return new TopicExchange(VOUCHER_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange investmentExchange() {
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue listingFundedQueue() {
        return QueueBuilder.durable(LISTING_FULLY_FUNDED_QUEUE).build();
    }

    @Bean
    public Queue investmentCancelledQueue() {
        return QueueBuilder.durable(INVESTMENT_CANCELLED_QUEUE).build();
    }

    @Bean
    public Binding listingFundedBinding(Queue listingFundedQueue,
                                        TopicExchange investmentExchange) {
        return BindingBuilder.bind(listingFundedQueue)
            .to(investmentExchange)
            .with(LISTING_FULLY_FUNDED_KEY);
    }

    @Bean
    public Binding investmentCancelledBinding(Queue investmentCancelledQueue,
                                              TopicExchange investmentExchange) {
        return BindingBuilder.bind(investmentCancelledQueue)
            .to(investmentExchange)
            .with(INVESTMENT_CANCELLED_KEY);
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
