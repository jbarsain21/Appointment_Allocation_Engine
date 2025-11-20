package com.booking_system.service;

import com.booking_system.entity.ServiceOffering;
import com.booking_system.repository.ServiceOfferingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceOfferingService {

    private final ServiceOfferingRepository serviceOfferingRepository;

    public ServiceOfferingService(ServiceOfferingRepository serviceOfferingRepository) {
        this.serviceOfferingRepository = serviceOfferingRepository;
    }

    // Basic CRUD operations
    public ServiceOffering createServiceOffering(ServiceOffering serviceOffering) {
        return serviceOfferingRepository.save(serviceOffering);
    }

    public List<ServiceOffering> getAllServiceOfferings() {
        return serviceOfferingRepository.findAll();
    }

    public Optional<ServiceOffering> getServiceOfferingById(String id) {
        return serviceOfferingRepository.findById(id);
    }

    public ServiceOffering updateServiceOffering(ServiceOffering serviceOffering) {
        return serviceOfferingRepository.save(serviceOffering);
    }

    public void deleteServiceOffering(String id) {
        serviceOfferingRepository.deleteById(id);
    }

    // Business methods for allocation algorithm
    public List<ServiceOffering> getServiceOfferingsByName(String name) {
        return serviceOfferingRepository.findByNameContainingIgnoreCase(name);
    }
}

