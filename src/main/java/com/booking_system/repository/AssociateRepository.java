package com.booking_system.repository;

import com.booking_system.entity.Associate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssociateRepository extends MongoRepository<Associate, String> {
    public List<Associate> findByStoreId(String storeId);
    public List<Associate> findByStatus(Associate.AssociateStatus status);
}
