package com.booking_system.events;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentConfirmedEvent {
    private String appointmentId;
    private String customerId;
    private String associateId;
    private String associateName;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime timestamp;

}
