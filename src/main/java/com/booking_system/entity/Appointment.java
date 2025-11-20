package com.booking_system.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "appointments")
public class Appointment {

    @Id
    private String id;

    // References to other entities
    private String customerId;    // Who booked it
    private String serviceOfferingId;     // What service they want
    private String storeId;    // Where it will happen
    private String associateId;   // Who will provide the service (allocated by algorithm)

    // Appointment timing
    private LocalDateTime appointmentDateTime;  // When it's scheduled
    private LocalDateTime endDateTime;          // When it ends (calculated from service duration)

    // Status tracking
    private AppointmentStatus status;

    // Customer notes/requirements
    private String customerNotes;

    // Allocation details (for algorithm tracking)
    private Double allocationScore;  // Score given by allocation algorithm
    private String allocationReason; // Why this associate was chose

    // Enum for appointment status
    public enum AppointmentStatus {
        PENDING,        // Just created, waiting for allocation
        CONFIRMED,      // Allocated to associate
        CHECKED_IN,     // Customer arrived
        IN_PROGRESS,    // Service being provided
        COMPLETED,      // Service finished
        CANCELLED,      // Cancelled by customer/system
        NO_SHOW        // Customer didn't show up
    }
}