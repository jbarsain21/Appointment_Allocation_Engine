package com.booking_system.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCancelledEvent {
    private String appointmentId;
    private String customerId;
    private String associateId;
    private LocalDateTime appointmentDateTime;
    private String storeId;
    private String cancellationReason;
    private LocalDateTime cancellationDateTime;
}
