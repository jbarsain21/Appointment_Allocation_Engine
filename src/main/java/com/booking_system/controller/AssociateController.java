package com.booking_system.controller;

import com.booking_system.entity.Associate;
import com.booking_system.service.AssociateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/associates")
public class AssociateController {

    @Autowired
    private AssociateService associateService;

    @PostMapping
    public Associate createAssociate(@RequestBody Associate associate) {
        return associateService.createAssociate(associate);
    }

    @GetMapping
    public List<Associate> getAllAssociates() {
        return associateService.getAllAssociates();
    }

    @GetMapping("/{id}")
    public Optional<Associate> getAssociateById(@PathVariable String id) {
        return associateService.getAssociateById(id);
    }

    @PutMapping("/{id}")
    public Associate updateAssociate(@PathVariable String id, @RequestBody Associate associate) {
        associate.setId(id);
        return associateService.updateAssociate(associate);
    }

    @DeleteMapping("/{id}")
    public void deleteAssociate(@PathVariable String id) {
        associateService.deleteAssociate(id);
    }

    // Business endpoints
    @GetMapping("/store/{storeId}")
    public List<Associate> getAssociatesByStore(@PathVariable String storeId) {
        return associateService.getAssociatesByStore(storeId);
    }

    @GetMapping("/active")
    public List<Associate> getActiveAssociates() {
        return associateService.getActiveAssociates();
    }
}
