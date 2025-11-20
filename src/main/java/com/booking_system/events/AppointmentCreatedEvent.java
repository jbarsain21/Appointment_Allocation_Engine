package com.booking_system.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCreatedEvent {
    private String appointmentId;
    private String customerId;
    private String serviceOfferingId;
    private String storeId;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime timestamp;
}
