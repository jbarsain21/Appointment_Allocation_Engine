package com.booking_system.repository;

import com.booking_system.entity.Store;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends MongoRepository<Store, String> {
    List<Store> findByCity(String city);
    List<Store> findByServiceOfferingIdsContaining(String serviceOfferingId);
}
