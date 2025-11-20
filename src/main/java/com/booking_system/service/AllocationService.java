package com.booking_system.service;

import com.booking_system.entity.Appointment;
import com.booking_system.entity.Associate;
import com.booking_system.entity.Store;
import com.booking_system.entity.ServiceOffering;
import com.booking_system.events.AppointmentConfirmedEvent;
import com.booking_system.messaging.publisher.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AllocationService {

    private static final Logger logger = LoggerFactory.getLogger(AllocationService.class);

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AssociateService associateService;

    @Autowired
    private ServiceOfferingService serviceOfferingService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private EventPublisher eventPublisher;


    public Appointment allocateAppointment(String appointmentId) {
        Optional<Appointment> appointmentOpt = appointmentService.getAppointmentById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return null;
        }

        Appointment appointment = appointmentOpt.get();

        // Find the best associate for this appointment
        Associate bestAssociate = findBestAssociate(appointment);

        if (bestAssociate != null) {
            // Calculate allocation score and reason
            double score = calculateAllocationScore(appointment, bestAssociate);
            String reason = generateAllocationReason(appointment, bestAssociate, score);

            // Allocate the appointment
            Appointment allocatedAppointment = appointmentService.allocateAppointment(appointmentId, bestAssociate.getId(), score, reason);
            
            // Publish confirmation event
            AppointmentConfirmedEvent confirmationEvent = new AppointmentConfirmedEvent();
            confirmationEvent.setAppointmentId(allocatedAppointment.getId());
            confirmationEvent.setCustomerId(allocatedAppointment.getCustomerId());
            confirmationEvent.setAssociateId(bestAssociate.getId());
            confirmationEvent.setAssociateName(bestAssociate.getFirstName() + " " + bestAssociate.getLastName());
            confirmationEvent.setAppointmentDateTime(allocatedAppointment.getAppointmentDateTime());
            confirmationEvent.setTimestamp(LocalDateTime.now());
            
            eventPublisher.publishAppointmentConfirmedEvent(confirmationEvent);
            
            return allocatedAppointment;
        }

        return null; // No suitable associate found
    }

    // Smart algorithm to find the best associate
    public Associate findBestAssociate(Appointment appointment) {
        // Get service requirements
        Optional<ServiceOffering> serviceOpt = serviceOfferingService.getServiceOfferingById(appointment.getServiceOfferingId());
        if (serviceOpt.isEmpty()) {
            return null;
        }
        ServiceOffering service = serviceOpt.get();

        // Get store
        Optional<Store> storeOpt = storeService.getStoreById(appointment.getStoreId());
        if (storeOpt.isEmpty()) {
            return null;
        }
        Store store = storeOpt.get();

        // Get associates at this store
        List<Associate> allAssociates = associateService.getAssociatesByStore(appointment.getStoreId());
        
        List<Associate> candidates = allAssociates.stream()
                .filter(associate -> associate.getStatus() == Associate.AssociateStatus.ACTIVE)
                .toList();

        Associate bestAssociate = null;
        double bestScore = -1;

        for (Associate associate : candidates) {
            double score = calculateAllocationScore(appointment, associate, service, store);
            if (score > bestScore) {
                bestScore = score;
                bestAssociate = associate;
            }
        }

        return bestAssociate;
    }

    // Sophisticated scoring algorithm
    private double calculateAllocationScore(Appointment appointment, Associate associate, ServiceOffering service, Store store) {
        double score = 0.0;

        // 1. Skill Matching (40% of score)
        double skillScore = calculateSkillMatch(service.getRequiredSkills(), associate.getSkills());
        score += skillScore * 0.4;

        // 2. Availability Check (30% of score)
        double availabilityScore = calculateAvailabilityScore(associate, appointment);
        score += availabilityScore * 0.3;

        // 3. Load Balancing (20% of score)
        double loadScore = calculateLoadBalanceScore(associate, appointment);
        score += loadScore * 0.2;

        // 4. Experience & Rating (10% of score)
        double experienceScore = calculateExperienceScore(associate);
        score += experienceScore * 0.1;

        return score;
    }

    // Calculate how well associate skills match service requirements
    double calculateSkillMatch(List<String> requiredSkills, List<String> associateSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return 1.0; // No specific skills required
        }

        if (associateSkills == null || associateSkills.isEmpty()) {
            return 0.0; // Associate has no skills
        }

        long matchingSkills = requiredSkills.stream()
                .filter(associateSkills::contains)
                .count();

        return (double) matchingSkills / requiredSkills.size();
    }

    // Check if associate is available at appointment time
    private double calculateAvailabilityScore(Associate associate, Appointment appointment) {
        LocalDateTime appointmentTime = appointment.getAppointmentDateTime();
        LocalTime appointmentTimeOnly = appointmentTime.toLocalTime();

        // Check working hours
        if (appointmentTimeOnly.isBefore(associate.getWorkStartTime()) ||
                appointmentTimeOnly.isAfter(associate.getWorkEndTime())) {
            return 0.0; // Outside working hours
        }

        // Check for conflicts
        LocalDateTime dayStart = appointmentTime.toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        List<Appointment> existingAppointments = appointmentService
                .getAssociateAppointmentsForDay(associate.getId(), dayStart, dayEnd);

        // Check for time conflicts
        for (Appointment existing : existingAppointments) {
            if (hasTimeConflict(appointment, existing)) {
                return 0.0; // Time conflict
            }
        }

        return 1.0; // Available
    }

    // Calculate load balance score (prefer associates with fewer appointments)
    private double calculateLoadBalanceScore(Associate associate, Appointment appointment) {
        LocalDateTime dayStart = appointment.getAppointmentDateTime().toLocalDate().atStartOfDay();
        LocalDateTime dayEnd = dayStart.plusDays(1);

        int dailyAppointments = appointmentService
                .getAssociateAppointmentsForDay(associate.getId(), dayStart, dayEnd).size();

        int maxAppointments = associate.getMaxAppointmentsPerDay() != null ?
                associate.getMaxAppointmentsPerDay() : 8; // Default max

        if (dailyAppointments >= maxAppointments) {
            return 0.0; // At capacity
        }

        // Higher score for associates with fewer appointments
        return 1.0 - ((double) dailyAppointments / maxAppointments);
    }

    // Calculate experience score
    private double calculateExperienceScore(Associate associate) {
        double experienceScore = 0.5; // Default

        if (associate.getExperienceLevel() != null) {
            switch (associate.getExperienceLevel()) {
                case EXPERT: experienceScore = 1.0; break;
                case SENIOR: experienceScore = 0.8; break;
                case JUNIOR: experienceScore = 0.6; break;
            }
        }

        // Factor in rating if available
        if (associate.getRating() != null) {
            experienceScore = (experienceScore + (associate.getRating() / 5.0)) / 2.0;
        }

        return experienceScore;
    }

    // Check for time conflicts between appointments
    private boolean hasTimeConflict(Appointment newAppointment, Appointment existingAppointment) {
        LocalDateTime newStart = newAppointment.getAppointmentDateTime();
        LocalDateTime newEnd = newAppointment.getEndDateTime();
        LocalDateTime existingStart = existingAppointment.getAppointmentDateTime();
        LocalDateTime existingEnd = existingAppointment.getEndDateTime();

        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    // Generate human-readable allocation reason
    private String generateAllocationReason(Appointment appointment, Associate associate, double score) {
        return String.format("Best match: %.1f%% score - Skills: %s, Experience: %s, Load: balanced",
                score * 100,
                associate.getSkills() != null ? String.join(", ", associate.getSkills()) : "General",
                associate.getExperienceLevel() != null ? associate.getExperienceLevel().toString() : "Standard");
    }

    // Overloaded method for existing code compatibility
    private double calculateAllocationScore(Appointment appointment, Associate associate) {
        Optional<ServiceOffering> serviceOpt = serviceOfferingService.getServiceOfferingById(appointment.getServiceOfferingId());
        Optional<Store> storeOpt = storeService.getStoreById(appointment.getStoreId());

        if (serviceOpt.isEmpty() || storeOpt.isEmpty()) {
            return 0.0;
        }

        return calculateAllocationScore(appointment, associate, serviceOpt.get(), storeOpt.get());
    }
}
