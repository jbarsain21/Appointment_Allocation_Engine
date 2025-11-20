package com.booking_system.service;

import com.booking_system.entity.Associate;
import com.booking_system.repository.AssociateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssociateService {

    private final AssociateRepository associateRepository;

    public AssociateService(AssociateRepository associateRepository) {
        this.associateRepository = associateRepository;
    }

    // Basic CRUD operations
    public Associate createAssociate(Associate associate) {
        return associateRepository.save(associate);
    }

    public List<Associate> getAllAssociates() {
        return associateRepository.findAll();
    }

    public Optional<Associate> getAssociateById(String id) {
        return associateRepository.findById(id);
    }

    public Associate updateAssociate(Associate associate) {
        return associateRepository.save(associate);
    }

    public void deleteAssociate(String id) {
        associateRepository.deleteById(id);
    }

    // Business methods for allocation algorithm
    public List<Associate> getAssociatesByStore(String storeId) {
        return associateRepository.findByStoreId(storeId);
    }

    public List<Associate> getActiveAssociates() {
        return associateRepository.findByStatus(Associate.AssociateStatus.ACTIVE);
    }
}
