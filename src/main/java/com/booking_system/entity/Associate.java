package com.booking_system.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalTime;
import java.util.List;

@Data
@Document(collection = "associates")
public class Associate {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    // Skills for allocation algorithm
    private List<String> skills;  // e.g., ["haircut", "coloring", "loan_consultation"]

    // Experience level (for allocation scoring)
    private ExperienceLevel experienceLevel;

    // Working hours
    private LocalTime workStartTime;  // e.g., 09:00
    private LocalTime workEndTime;    // e.g., 17:00

    // Store where they work
    private String storeId;  // Reference to Store

    // Rating (for allocation algorithm)
    private Double rating;  // 1.0 to 5.0

    // Status
    private AssociateStatus status;

    // Maximum appointments per day
    private Integer maxAppointmentsPerDay;

    // Enums
    public enum ExperienceLevel {
        JUNIOR, SENIOR, EXPERT
    }

    public enum AssociateStatus {
        ACTIVE, INACTIVE, ON_LEAVE
    }
}