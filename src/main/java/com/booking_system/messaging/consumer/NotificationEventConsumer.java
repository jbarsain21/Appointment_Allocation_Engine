package com.booking_system.messaging.consumer;

import com.booking_system.events.AppointmentConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);
    
    @RabbitListener(queues = {"${rabbitmq.notification.queue.name}"})
    public void handleAppointmentConfirmed(AppointmentConfirmedEvent event) {
        logger.info("Appointment confirmed event received: " + event);
        logger.info("Sending confirmation to customer: " + event.getCustomerId());
        logger.info("Appointment confirmed with " + event.getAssociateName());
        logger.info("Appointment time: " + event.getAppointmentDateTime());
        logger.info("Notification sent successfully!");
    }
}
