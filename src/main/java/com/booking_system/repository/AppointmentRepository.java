package com.booking_system.repository;

import com.booking_system.entity.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByCustomerId(String customerId);
    List<Appointment> findByAssociateId(String associateId);
    List<Appointment> findByStoreId(String storeId);
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByAssociateIdAndAppointmentDateTimeBetween(String associateId, LocalDateTime start, LocalDateTime end);
}
