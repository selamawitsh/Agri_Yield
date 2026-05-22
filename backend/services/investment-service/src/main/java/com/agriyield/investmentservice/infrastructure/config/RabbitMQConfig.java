package com.agriyield.investmentservice.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INVESTMENT_EXCHANGE           = "investment.exchange";
    public static final String INVESTMENT_PLACED_KEY         = "investment.placed";
    public static final String INVESTMENT_ESCROW_LOCKED_KEY  = "investment.escrow.locked";
    public static final String INVESTMENT_CANCELLED_KEY      = "investment.cancelled";
    public static final String INVESTMENT_COMPLETED_KEY      = "investment.completed";

    @Bean
    public TopicExchange investmentExchange() {
        return new TopicExchange(INVESTMENT_EXCHANGE, true, false);
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
