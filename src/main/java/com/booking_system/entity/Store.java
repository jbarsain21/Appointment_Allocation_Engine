package com.booking_system.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.List;

@Data
@Document(collection = "stores")
public class Store implements Serializable {

    @Id
    private String id;

    private String name;        // e.g., "Downtown Branch", "Mall Salon", "Service Center North"
    private String address;     // Full address
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;

    // Business hours - ESSENTIAL for scheduling
    private LocalTime openTime;
    private LocalTime closeTime;

    // Services offered at this store
    private List<String> serviceOfferingIds;  // References to ServiceOffering entities
}
