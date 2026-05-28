package com.agriyield.weatherservice.infrastructure.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange — SRS Section 2.2.3
    public static final String WEATHER_EXCHANGE       = "weather.exchange";

    // Routing keys — SRS Section 5.2
    public static final String WEATHER_ALERT_KEY      = "weather.alert";
    public static final String DROUGHT_TRIGGERED_KEY  = "drought.triggered";

    @Bean
    public TopicExchange weatherExchange() {
        return new TopicExchange(WEATHER_EXCHANGE, true, false);
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
