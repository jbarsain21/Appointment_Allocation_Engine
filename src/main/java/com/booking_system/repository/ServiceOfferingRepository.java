package com.booking_system.repository;

import com.booking_system.entity.ServiceOffering;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOfferingRepository extends MongoRepository<ServiceOffering, String> {
    List<ServiceOffering> findByNameContainingIgnoreCase(String name);
}
