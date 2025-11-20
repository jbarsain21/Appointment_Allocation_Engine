package com.booking_system.messaging.consumer;

import com.booking_system.events.AppointmentCreatedEvent;
import com.booking_system.service.AllocationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AllocationEventConsumer {

    @Autowired
    private AllocationService allocationService;

    private static final Logger logger = LoggerFactory.getLogger(AllocationEventConsumer.class);

    @RabbitListener(queues = {"${rabbitmq.queue.name}"})
    public void handleAppointmentCreated(AppointmentCreatedEvent event){
        logger.info("Appointment created event received: " + event);
        allocationService.allocateAppointment(event.getAppointmentId());
    }
}
