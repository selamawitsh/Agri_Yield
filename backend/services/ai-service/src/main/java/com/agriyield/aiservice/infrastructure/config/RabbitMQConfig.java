package com.agriyield.aiservice.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // farm.exchange — consumed FROM farm-service
    public static final String FARM_EXCHANGE            = "farm.exchange";
    public static final String CROP_PHOTO_UPLOADED_KEY  = "crop.photo.uploaded";
    public static final String AI_PHOTO_EVENTS_QUEUE    = "ai.photo-events.queue";
    // Event routing key for agronomist booking requests
    public static final String AGRONOMIST_BOOKING_KEY   = "agronomist.booking.required";

    @Bean
    public TopicExchange farmExchange() {
        return new TopicExchange(FARM_EXCHANGE, true, false);
    }

    @Bean
    public Queue aiPhotoEventsQueue() {
        return QueueBuilder.durable(AI_PHOTO_EVENTS_QUEUE).build();
    }

    @Bean
    public Binding cropPhotoBinding(Queue aiPhotoEventsQueue,
                                    TopicExchange farmExchange) {
        return BindingBuilder.bind(aiPhotoEventsQueue)
                .to(farmExchange)
                .with(CROP_PHOTO_UPLOADED_KEY);
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
