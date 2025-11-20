package com.booking_system.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customers")
@Data
public class Customer {
    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
