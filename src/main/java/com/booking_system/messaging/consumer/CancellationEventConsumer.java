package com.booking_system.messaging.consumer;

import com.booking_system.events.AppointmentCancelledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CancellationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(CancellationEventConsumer.class);

    @RabbitListener(queues = "${rabbitmq.cancellation.queue.name}")
    public void receiveCancellationEvent(AppointmentCancelledEvent event){
        logger.info("Received cancellation event: " + event);
    }
}
