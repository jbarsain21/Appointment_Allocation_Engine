package com.booking_system.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "service_offerings")
public class ServiceOffering {

    @Id
    private String id;

    private String name;          // e.g., "Haircut", "Loan Consultation", "AC Repair"
    private String description;   // Detailed description

    // Duration and pricing
    private Integer durationMinutes;  // e.g., 30, 60, 90 minutes
    private Double price;            // Service price

    // Skills required (for allocation algorithm)
    private List<String> requiredSkills;  // e.g., ["haircut"], ["loan_consultation", "credit_analysis"]
}