package com.booking_system.integration;
import com.booking_system.dto.BookingRequest;
import com.booking_system.entity.Customer;
import com.booking_system.entity.ServiceOffering;
import com.booking_system.entity.Store;
import com.booking_system.repository.AppointmentRepository;
import com.booking_system.repository.CustomerRepository;
import com.booking_system.repository.ServiceOfferingRepository;
import com.booking_system.repository.StoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AppointmentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    private Customer testCustomer;
    private ServiceOffering testService;
    private Store testStore;

    @BeforeEach
    void setUp() {
        // Clean up data
        // appointmentRepository.deleteAll();
        
        // Create test customer
        testCustomer = new Customer();
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john.doe@example.com");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer = customerRepository.save(testCustomer);

        // Create test service offering
        testService = new ServiceOffering();
        testService.setName("Hair Cut");
        testService.setDurationMinutes(60);
        testService.setPrice(25.0);
        testService = serviceOfferingRepository.save(testService);

        // Create test store
        testStore = new Store();
        testStore.setName("Main Branch");
        testStore.setAddress("123 Main St");
        testStore.setPhoneNumber("555-0123");
        testStore = storeRepository.save(testStore);
    }

    // SUCCESS SCENARIOS
    @Test
    void bookAppointment_ValidRequest_ShouldCreateAppointment() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", containsString("Appointment created successfully")))
                .andExpect(jsonPath("$.appointment.customerId", is(testCustomer.getId())))
                .andExpect(jsonPath("$.appointment.serviceOfferingId", is(testService.getId())))
                .andExpect(jsonPath("$.appointment.storeId", is(testStore.getId())))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void bookAppointment_WithCustomerNotes_ShouldCreateAppointmentWithNotes() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setCustomerNotes("Please use organic products");

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.appointment.customerNotes", is("Please use organic products")));
    }

    // VALIDATION ERROR SCENARIOS
    @Test
    void bookAppointment_MissingCustomerId_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setCustomerId(null);

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Customer ID is required")));
    }

    @Test
    void bookAppointment_EmptyCustomerId_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setCustomerId("");

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Customer ID is required")));
    }

    @Test
    void bookAppointment_MissingServiceOfferingId_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setServiceOfferingId(null);

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Service offering ID is required")));
    }

    @Test
    void bookAppointment_MissingStoreId_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setStoreId(null);

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Store ID is required")));
    }

    @Test
    void bookAppointment_MissingAppointmentDateTime_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setAppointmentDateTime(null);

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Appointment date/time is required")));
    }

    @Test
    void bookAppointment_AppointmentTooSoon_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setAppointmentDateTime(LocalDateTime.now().plusMinutes(30)); // Less than 1 hour

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Appointments must be booked at least 1 hour in advance")));
    }

    @Test
    void bookAppointment_AppointmentTooFar_ShouldReturnBadRequest() throws Exception {
        // Given
        BookingRequest request = createValidBookingRequest();
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(31)); // More than 30 days

        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Appointments cannot be booked more than 30 days in advance")));
    }

    @Test
    void bookAppointment_CustomerHasMaxActiveBookings_ShouldReturnBadRequest() throws Exception {
        // Given - Create 3 active appointments for the customer
        for (int i = 0; i < 3; i++) {
            BookingRequest request = createValidBookingRequest();
            request.setAppointmentDateTime(LocalDateTime.now().plusHours(2 + i));
            
            mockMvc.perform(post("/appointments/book")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));
        }

        // When - Try to book 4th appointment
        BookingRequest request = createValidBookingRequest();
        request.setAppointmentDateTime(LocalDateTime.now().plusHours(6));

        // Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Customer has reached maximum of 3 active bookings")));
    }

    @Test
    void bookAppointment_InvalidJson_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/appointments/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    // HELPER METHODS
    private BookingRequest createValidBookingRequest() {
        BookingRequest request = new BookingRequest();
        request.setCustomerId(testCustomer.getId());
        request.setServiceOfferingId(testService.getId());
        request.setStoreId(testStore.getId());
        request.setAppointmentDateTime(LocalDateTime.now().plusHours(2)); // 2 hours from now
        return request;
    }
}
