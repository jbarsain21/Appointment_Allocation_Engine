package com.booking_system.controller;

import com.booking_system.dto.BookingRequest;
import com.booking_system.entity.Appointment;
import com.booking_system.entity.ServiceOffering;
import com.booking_system.events.AppointmentCreatedEvent;
import com.booking_system.messaging.publisher.EventPublisher;
import com.booking_system.service.AllocationService;
import com.booking_system.service.AppointmentService;
import com.booking_system.service.ServiceOfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private ServiceOfferingService serviceOfferingService;

    @Autowired
    private EventPublisher eventPublisher;

    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public Optional<Appointment> getAppointmentById(@PathVariable String id) {
        return appointmentService.getAppointmentById(id);
    }

    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable String id, @RequestBody Appointment appointment) {
        appointment.setId(id);
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
    }

    // Business endpoints
    @GetMapping("/customer/{customerId}")
    public List<Appointment> getAppointmentsByCustomer(@PathVariable String customerId) {
        return appointmentService.getAppointmentsByCustomer(customerId);
    }

    @GetMapping("/associate/{associateId}")
    public List<Appointment> getAppointmentsByAssociate(@PathVariable String associateId) {
        return appointmentService.getAppointmentsByAssociate(associateId);
    }

    @GetMapping("/pending")
    public List<Appointment> getPendingAppointments() {
        return appointmentService.getPendingAppointments();
    }

    // Allocation endpoint - this will connect to your AllocationService later
    @PostMapping("/{appointmentId}/allocate/{associateId}")
    public Appointment allocateAppointment(
            @PathVariable String appointmentId,
            @PathVariable String associateId,
            @RequestParam(defaultValue = "0.0") double score,
            @RequestParam(defaultValue = "Manual allocation") String reason) {
        return appointmentService.allocateAppointment(appointmentId, associateId, score, reason);
    }

    // Add this new endpoint for smart booking
    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestBody BookingRequest bookingRequest) {
        try {
            // 1. Validate booking request
            String validationError = validateBookingRequest(bookingRequest);
            if (validationError != null) {
                return ResponseEntity.badRequest().body(Map.of("error", validationError));
            }

            // 2. Create appointment in PENDING status
            Appointment appointment = new Appointment();
            appointment.setCustomerId(bookingRequest.getCustomerId());
            appointment.setServiceOfferingId(bookingRequest.getServiceOfferingId());
            appointment.setStoreId(bookingRequest.getStoreId());
            appointment.setAppointmentDateTime(bookingRequest.getAppointmentDateTime());
            appointment.setCustomerNotes(bookingRequest.getCustomerNotes());

            // Calculate end time based on service duration
            appointment.setEndDateTime(calculateEndTime(bookingRequest));

            // Create the appointment
            Appointment createdAppointment = appointmentService.createAppointment(appointment);

            //Create an appointment created event
            AppointmentCreatedEvent event = new AppointmentCreatedEvent();
            event.setAppointmentId(createdAppointment.getId());
            event.setAppointmentDateTime(createdAppointment.getAppointmentDateTime());
            event.setCustomerId(createdAppointment.getCustomerId());
            event.setServiceOfferingId(createdAppointment.getServiceOfferingId());
            event.setStoreId(createdAppointment.getStoreId());
            event.setTimestamp(LocalDateTime.now());
            eventPublisher.publishAppointmentCreatedEvent(event);

            // Return immediate response - allocation happens asynchronously
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment created successfully. Allocation in progress...",
                    "appointment", createdAppointment,
                    "status", "PENDING"
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Booking failed: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable String appointmentId,
                                                                 @RequestBody(required = false) Map<String, String> cancellationRequest){
        try {
            String reason = cancellationRequest!=null?cancellationRequest.get("reason"):"No reason provided";
            boolean cancelled = appointmentService.cancelAppointment(appointmentId, reason);
            if (cancelled) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Appointment cancelled successfully.",
                        "appointmentId", appointmentId
                ));
            }
            else{
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Unable to cancel appointment. Check cancellation policy."
                ));
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Cancellation failed: " + e.getMessage()
            ));
        }
    }

    // Validation method
    private String validateBookingRequest(BookingRequest request) {
        System.out.println(request);
        if (request.getCustomerId() == null || request.getCustomerId().isEmpty()) {
            return "Customer ID is required";
        }

        if (request.getServiceOfferingId() == null || request.getServiceOfferingId().isEmpty()) {
            return "Service offering ID is required";
        }

        if (request.getStoreId() == null || request.getStoreId().isEmpty()) {
            return "Store ID is required";
        }

        if (request.getAppointmentDateTime() == null) {
            return "Appointment date/time is required";
        }

        // Check booking time constraints (1 hour to 30 days in advance)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentTime = request.getAppointmentDateTime();

        if (appointmentTime.isBefore(now.plusHours(1))) {
            return "Appointments must be booked at least 1 hour in advance";
        }

        if (appointmentTime.isAfter(now.plusDays(30))) {
            return "Appointments cannot be booked more than 30 days in advance";
        }

        // Check customer booking limit (max 3 active bookings)
        int activeBookings = appointmentService.getActiveAppointmentCountForCustomer(request.getCustomerId());
        if (activeBookings >= 3) {
            return "Customer has reached maximum of 3 active bookings";
        }

        return null; // No validation errors
    }

    // Calculate end time based on service duration
    private LocalDateTime calculateEndTime(BookingRequest request) {
        // Get service duration
        Optional<ServiceOffering> serviceOpt = serviceOfferingService.getServiceOfferingById(request.getServiceOfferingId());
        if (serviceOpt.isPresent() && serviceOpt.get().getDurationMinutes() != null) {
            return request.getAppointmentDateTime().plusMinutes(serviceOpt.get().getDurationMinutes());
        }

        // Default to 60 minutes if no duration specified
        return request.getAppointmentDateTime().plusMinutes(60);
    }
}
