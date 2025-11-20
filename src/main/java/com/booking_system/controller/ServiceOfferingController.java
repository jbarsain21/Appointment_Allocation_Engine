package com.booking_system.controller;

import com.booking_system.entity.ServiceOffering;
import com.booking_system.service.ServiceOfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service-offerings")
public class ServiceOfferingController {

    @Autowired
    private ServiceOfferingService serviceOfferingService;

    @PostMapping
    public ServiceOffering createServiceOffering(@RequestBody ServiceOffering serviceOffering) {
        return serviceOfferingService.createServiceOffering(serviceOffering);
    }

    @GetMapping
    public List<ServiceOffering> getAllServiceOfferings() {
        return serviceOfferingService.getAllServiceOfferings();
    }

    @GetMapping("/{id}")
    public Optional<ServiceOffering> getServiceOfferingById(@PathVariable String id) {
        return serviceOfferingService.getServiceOfferingById(id);
    }

    @PutMapping("/{id}")
    public ServiceOffering updateServiceOffering(@PathVariable String id, @RequestBody ServiceOffering serviceOffering) {
        serviceOffering.setId(id);
        return serviceOfferingService.updateServiceOffering(serviceOffering);
    }

    @DeleteMapping("/{id}")
    public void deleteServiceOffering(@PathVariable String id) {
        serviceOfferingService.deleteServiceOffering(id);
    }

    // Business endpoints
    @GetMapping("/search")
    public List<ServiceOffering> searchServiceOfferingsByName(@RequestParam String name) {
        return serviceOfferingService.getServiceOfferingsByName(name);
    }
}
