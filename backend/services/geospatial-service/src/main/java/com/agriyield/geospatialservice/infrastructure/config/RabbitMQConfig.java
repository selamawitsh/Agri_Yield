package com.agriyield.geospatialservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange this service publishes to
    public static final String GEOSPATIAL_EXCHANGE   = "geospatial.exchange";
    public static final String NDVI_UPDATED_KEY      = "ndvi.updated";
    public static final String YIELD_PREDICTED_KEY   = "yield.predicted";
    public static final String HARVEST_PREDICTED_KEY = "harvest.predicted";

    // Farm exchange this service listens to
    public static final String FARM_EXCHANGE              = "farm.exchange";
    public static final String FARM_REGISTERED_QUEUE      = "geospatial.farm-registered.queue";
    public static final String FARM_REGISTERED_KEY        = "farm.registered";

    @Bean
    public TopicExchange geospatialExchange() {
        return new TopicExchange(GEOSPATIAL_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }

    @Bean
    public Queue farmRegisteredQueue() {
        return QueueBuilder.durable(FARM_REGISTERED_QUEUE).build();
    }

    @Bean
    public Binding farmRegisteredBinding(Queue farmRegisteredQueue,
                                          TopicExchange farmExchange) {
        return BindingBuilder.bind(farmRegisteredQueue)
            .to(farmExchange).with(FARM_REGISTERED_KEY);
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
