package com.booking_system.messaging.publisher;

import com.booking_system.events.AppointmentCancelledEvent;
import com.booking_system.events.AppointmentConfirmedEvent;
import com.booking_system.events.AppointmentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    
    @Value("${rabbitmq.notification.routing.key}")
    private String notificationRoutingKey;

    @Value("${rabbitmq.cancellation.routing.key}")
    private String cancellationRoutingKey;

    private static final Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAppointmentCreatedEvent(AppointmentCreatedEvent event){
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        logger.info("Appointment created event published successfully");
    }
    
    public void publishAppointmentConfirmedEvent(AppointmentConfirmedEvent event){
        rabbitTemplate.convertAndSend(exchange, notificationRoutingKey, event);
        logger.info("Appointment confirmed event published successfully");
    }

    public void publishAppointmentCancelledEvent(AppointmentCancelledEvent event){
        rabbitTemplate.convertAndSend(exchange, cancellationRoutingKey, event);
        logger.info("Appointment cancelled event published successfully");
    }
}
