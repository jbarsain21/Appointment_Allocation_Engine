package com.booking_system.controller;

import com.booking_system.entity.Store;
import com.booking_system.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @PostMapping
    public Store createStore(@RequestBody Store store) {
        return storeService.createStore(store);
    }

    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @GetMapping("/{id}")
    public Optional<Store> getStoreById(@PathVariable String id) {
        return storeService.getStoreById(id);
    }

    @PutMapping("/{id}")
    public Store updateStore(@PathVariable String id, @RequestBody Store store) {
        store.setId(id);
        return storeService.updateStore(store);
    }

    @DeleteMapping("/{id}")
    public void deleteStore(@PathVariable String id) {
        storeService.deleteStore(id);
    }

    // Business endpoints
    @GetMapping("/city/{city}")
    public List<Store> getStoresByCity(@PathVariable String city) {
        return storeService.getStoresByCity(city);
    }

    @GetMapping("/service-offering/{serviceOfferingId}")
    public List<Store> getStoresByServiceOffering(@PathVariable String serviceOfferingId) {
        return storeService.getStoresByServiceOffering(serviceOfferingId);
    }
}

