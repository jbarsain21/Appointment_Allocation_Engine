package com.booking_system.service;

import com.booking_system.entity.Store;
import com.booking_system.repository.StoreRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private final StoreRepository storeRepository;

    public StoreService(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    // Basic CRUD operations
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    // @Cacheable(value = "stores", key = "#id") // Temporarily disabled due to deserialization issues
    public Optional<Store> getStoreById(String id) {
        return storeRepository.findById(id);
    }

    public Store updateStore(Store store) {
        return storeRepository.save(store);
    }

    public void deleteStore(String id) {
        storeRepository.deleteById(id);
    }

    // Business methods for allocation algorithm
    public List<Store> getStoresByCity(String city) {
        return storeRepository.findByCity(city);
    }

    public List<Store> getStoresByServiceOffering(String serviceOfferingId) {
        return storeRepository.findByServiceOfferingIdsContaining(serviceOfferingId);
    }
}

