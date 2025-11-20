package com.booking_system.service;

import com.booking_system.entity.Appointment;
import com.booking_system.entity.Associate;
import com.booking_system.events.AppointmentCancelledEvent;
import com.booking_system.messaging.publisher.EventPublisher;
import com.booking_system.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EventPublisher eventPublisher;

    // Basic CRUD operations
    public Appointment createAppointment(Appointment appointment) {
        // Set initial status as PENDING (waiting for allocation)
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(String id) {
        return appointmentRepository.findById(id);
    }

    public Appointment updateAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }

    // Business methods for allocation algorithm and management
    public List<Appointment> getAppointmentsByCustomer(String customerId) {
        return appointmentRepository.findByCustomerId(customerId);
    }

    public List<Appointment> getAppointmentsByAssociate(String associateId) {
        return appointmentRepository.findByAssociateId(associateId);
    }

    public List<Appointment> getAppointmentsByStore(String storeId) {
        return appointmentRepository.findByStoreId(storeId);
    }

    public List<Appointment> getPendingAppointments() {
        return appointmentRepository.findByStatus(Appointment.AppointmentStatus.PENDING);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end);
    }

    // Critical method for allocation algorithm - check associate availability
    public List<Appointment> getAssociateAppointmentsForDay(String associateId, LocalDateTime dayStart, LocalDateTime dayEnd) {
        return appointmentRepository.findByAssociateIdAndAppointmentDateTimeBetween(associateId, dayStart, dayEnd);
    }

    // Business logic methods
    public int getActiveAppointmentCountForCustomer(String customerId) {
        return (int) appointmentRepository.findByCustomerId(customerId).stream()
                .filter(appointment ->
                        appointment.getStatus() == Appointment.AppointmentStatus.CONFIRMED ||
                                appointment.getStatus() == Appointment.AppointmentStatus.CHECKED_IN ||
                                appointment.getStatus() == Appointment.AppointmentStatus.IN_PROGRESS)
                .count();
    }

    // Allocation method - this will be enhanced with the allocation engine
    public Appointment allocateAppointment(String appointmentId, String associateId, double score, String reason) {
        Optional<Appointment> appointmentOpt = getAppointmentById(appointmentId);
        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            appointment.setAssociateId(associateId);
            appointment.setAllocationScore(score);
            appointment.setAllocationReason(reason);
            appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
            return updateAppointment(appointment);
        }
        return null;
    }

    public boolean cancelAppointment(String appointmentId, String reason){
        Optional<Appointment> appointmentOpt = getAppointmentById(appointmentId);
        if(appointmentOpt.isEmpty()){
            throw new RuntimeException("Appointment not found");
        }

        Appointment appointment = appointmentOpt.get();

        // 2. Check if appointment can be cancelled (business rules)
        if (!canBeCancelled(appointment)) {
            return false; // Cannot cancel (too late, already completed, etc.)
        }

        // 3. Update appointment status to CANCELLED
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        // 4. Publish cancellation event for async processing
        publishCancellationEvent(appointment, reason);
        return true;
    }

    boolean canBeCancelled(Appointment appointment) {
        // Business rule: Can only cancel if appointment is more than 2 hours away
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();

        // Check if appointment is at least 2 hours in the future
        return appointmentTime.isAfter(now.plusHours(2)) &&
                appointment.getStatus() == Appointment.AppointmentStatus.CONFIRMED;
    }

    private void publishCancellationEvent(Appointment appointment, String reason) {
        AppointmentCancelledEvent event = new AppointmentCancelledEvent();
        event.setAppointmentId(appointment.getId());
        event.setCustomerId(appointment.getCustomerId());
        event.setAssociateId(appointment.getAssociateId());
        event.setAppointmentDateTime(appointment.getAppointmentDateTime());
        event.setStoreId(appointment.getStoreId());
        event.setCancellationReason(reason);
        event.setCancellationDateTime(LocalDateTime.now());

        eventPublisher.publishAppointmentCancelledEvent(event);
    }
}
