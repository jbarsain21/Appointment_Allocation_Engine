package com.booking_system.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private String customerId;
    private String serviceOfferingId;
    private String storeId;
    private LocalDateTime appointmentDateTime;
    private String customerNotes;
}
