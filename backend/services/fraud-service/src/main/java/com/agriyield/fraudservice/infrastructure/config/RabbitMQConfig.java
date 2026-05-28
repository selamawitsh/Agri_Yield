package com.agriyield.fraudservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange this service publishes to
    public static final String FRAUD_EXCHANGE           = "fraud.exchange";
    public static final String FRAUD_ALERT_HIGH_KEY     = "fraud.alert.high";
    public static final String FRAUD_ALERT_CRITICAL_KEY = "fraud.alert.critical";

    // Voucher exchange this service listens to
    public static final String VOUCHER_EXCHANGE              = "voucher.exchange";
    public static final String VOUCHER_REDEEMED_QUEUE        = "fraud.voucher-redeemed.queue";
    public static final String VOUCHER_REDEEMED_KEY          = "voucher.redeemed";
    public static final String VOUCHER_REJECTED_QUEUE        = "fraud.voucher-rejected.queue";
    public static final String VOUCHER_REJECTED_KEY          = "voucher.rejected";

    @Bean
    public TopicExchange fraudExchange() {
        return new TopicExchange(FRAUD_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange voucherExchange() {
        return new TopicExchange(VOUCHER_EXCHANGE, true, false);
    }

    @Bean
    public Queue voucherRedeemedQueue() {
        return QueueBuilder.durable(VOUCHER_REDEEMED_QUEUE).build();
    }

    @Bean
    public Queue voucherRejectedQueue() {
        return QueueBuilder.durable(VOUCHER_REJECTED_QUEUE).build();
    }

    @Bean
    public Binding voucherRedeemedBinding(Queue voucherRedeemedQueue,
                                           TopicExchange voucherExchange) {
        return BindingBuilder.bind(voucherRedeemedQueue)
            .to(voucherExchange).with(VOUCHER_REDEEMED_KEY);
    }

    @Bean
    public Binding voucherRejectedBinding(Queue voucherRejectedQueue,
                                           TopicExchange voucherExchange) {
        return BindingBuilder.bind(voucherRejectedQueue)
            .to(voucherExchange).with(VOUCHER_REJECTED_KEY);
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
