package com.booking_system.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    
    @Value("${rabbitmq.notification.queue.name}")
    private String notificationQueue;
    
    @Value("${rabbitmq.notification.routing.key}")
    private String notificationRoutingKey;

    @Value("${rabbitmq.cancellation.queue.name}")
    private String cancellationQueue;

    @Value("${rabbitmq.cancellation.routing.key}")
    private String cancellationRoutingKey;

    @Bean
    public Queue appointmentCreatedQueue(){
        return new Queue(queue);
    }

    @Bean
    public TopicExchange appointmentCreatedExchange(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding appointmentCreatedBinding(){
        return BindingBuilder.bind(appointmentCreatedQueue()).to(appointmentCreatedExchange()).with(routingKey);
    }
    
    @Bean
    public Queue appointmentConfirmedQueue(){
        return new Queue(notificationQueue);
    }
    
    @Bean
    public Binding appointmentConfirmedBinding(){
        return BindingBuilder.bind(appointmentConfirmedQueue()).to(appointmentCreatedExchange()).with(notificationRoutingKey);
    }

    @Bean
    public Queue appointmentCancelledQueue(){
        return new Queue(cancellationQueue);
    }

    @Bean
    public Binding appointmentCancelledBinding(){
        return BindingBuilder.bind(appointmentCancelledQueue()).to(appointmentCreatedExchange()).with(cancellationRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        
        // Force declaration of queues and exchanges
        admin.declareQueue(appointmentCreatedQueue());
        admin.declareQueue(appointmentConfirmedQueue());
        admin.declareQueue(appointmentCancelledQueue());
        admin.declareExchange(appointmentCreatedExchange());
        admin.declareBinding(appointmentCreatedBinding());
        admin.declareBinding(appointmentConfirmedBinding());
        admin.declareBinding(appointmentCancelledBinding());
        
        return admin;
    }
}

