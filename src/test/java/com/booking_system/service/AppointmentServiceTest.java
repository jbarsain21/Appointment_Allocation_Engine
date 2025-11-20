package com.booking_system.service;

import com.booking_system.entity.Appointment;
import com.booking_system.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    // ========== Tests for getActiveAppointmentCountForCustomer ==========
    
    @Test
    public void getActiveAppointmentCountForCustomer_AllActiveStatuses() {
        // Given: Customer has appointments with all active statuses
        String customerId = "customer123";
        List<Appointment> appointments = Arrays.asList(
            createAppointment("1", customerId, Appointment.AppointmentStatus.CONFIRMED),
            createAppointment("2", customerId, Appointment.AppointmentStatus.CHECKED_IN),
            createAppointment("3", customerId, Appointment.AppointmentStatus.IN_PROGRESS)
        );
        
        when(appointmentRepository.findByCustomerId(customerId)).thenReturn(appointments);
        
        // When
        int activeCount = appointmentService.getActiveAppointmentCountForCustomer(customerId);
        
        // Then
        assertEquals(3, activeCount, "Should count all active appointments");
    }

    @Test
    public void getActiveAppointmentCountForCustomer_NoActiveStatuses() {
        // Given: Customer has appointments with all active statuses
        String customerId = "customer123";
        List<Appointment> appointments = Arrays.asList(
                createAppointment("1", customerId, Appointment.AppointmentStatus.COMPLETED),
                createAppointment("2", customerId, Appointment.AppointmentStatus.CANCELLED),
                createAppointment("3", customerId, Appointment.AppointmentStatus.PENDING)
        );

        when(appointmentRepository.findByCustomerId(customerId)).thenReturn(appointments);

        // When
        int activeCount = appointmentService.getActiveAppointmentCountForCustomer(customerId);

        // Then
        assertEquals(0, activeCount, "Should count all active appointments");
    }


    // ========== Tests for canBeCancelled (existing) ==========

    @Test
    public void canBeCancelled_ValidScenario(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentDateTime(getValidFutureTime());
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        boolean canCancel = appointmentService.canBeCancelled(appointment);
        assertTrue(canCancel);
    }

    @Test
    public void canBeCancelled_TooLate(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentDateTime(getInvalidFutureTime());
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        boolean canCancel = appointmentService.canBeCancelled(appointment);
        assertFalse(canCancel);
    }

    @Test
    public void canBeCancelled_ExactlyTwoHours(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentDateTime(LocalDateTime.now().plusHours(2)); // Edge case
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        boolean canCancel = appointmentService.canBeCancelled(appointment);
        assertFalse(canCancel); // Should reject exactly 2 hours
    }

    @Test
    public void canBeCancelled_WrongStatus(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentDateTime(getValidFutureTime());
        appointment.setStatus(getInvalidStatus());
        boolean canCancel = appointmentService.canBeCancelled(appointment);
        assertFalse(canCancel);
    }

    @Test
    public void canBeCancelled_BothInvalid(){
        Appointment appointment = new Appointment();
        appointment.setAppointmentDateTime(getInvalidFutureTime()); // Bad time
        appointment.setStatus(getInvalidStatus());                  // Bad status
        boolean canCancel = appointmentService.canBeCancelled(appointment);
        assertFalse(canCancel); // Should still be false
    }


    
    // ========== Helper Methods ==========

    private LocalDateTime getValidFutureTime() {
        // Random time between 3-10 hours in future
        int randomHours = 3 + (int)(Math.random() * 8); // 3 to 10 hours
        return LocalDateTime.now().plusHours(randomHours);
    }

    private LocalDateTime getInvalidFutureTime() {
        // Random time between 0-2 hours in future
        int randomMinutes = (int)(Math.random() * 120); // 0 to 120 minutes
        return LocalDateTime.now().plusMinutes(randomMinutes);
    }

    private Appointment.AppointmentStatus getInvalidStatus() {
        // Any status except CONFIRMED
        Appointment.AppointmentStatus[] invalidStatuses = {
                Appointment.AppointmentStatus.PENDING,
                Appointment.AppointmentStatus.CANCELLED,
                Appointment.AppointmentStatus.COMPLETED
        };
        int randomIndex = (int)(Math.random() * invalidStatuses.length);
        return invalidStatuses[randomIndex];
    }

    
    /**
     * Helper method to create appointment with specific status
     */

    private Appointment createAppointment(String id, String customerId, Appointment.AppointmentStatus status) {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setCustomerId(customerId);
        appointment.setStatus(status);
        return appointment;
    }
}
