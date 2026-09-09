package com.care.medi.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CustomExceptionsTest {

    @Test
    @DisplayName("Should test BusinessException")
    void testBusinessException() {
        BusinessException ex = new BusinessException("Business error");
        assertEquals("Business error", ex.getMessage());
    }

    @Test
    @DisplayName("Should test AppointmentNotFoundException")
    void testAppointmentNotFoundException() {
        AppointmentNotFoundException ex1 = new AppointmentNotFoundException("Appointment not found");
        assertEquals("Appointment not found", ex1.getMessage());

        Exception cause = new RuntimeException("DB error");
        AppointmentNotFoundException ex2 = new AppointmentNotFoundException("Appointment error", cause);
        assertEquals("Appointment error", ex2.getMessage());
        assertEquals(cause, ex2.getCause());
    }

    @Test
    @DisplayName("Should test DoctorNotFoundException")
    void testDoctorNotFoundException() {
        DoctorNotFoundException ex = new DoctorNotFoundException("Doctor not found");
        assertEquals("Doctor not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should test DuplicateResourceException")
    void testDuplicateResourceException() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate entity");
        assertEquals("Duplicate entity", ex.getMessage());
    }

    @Test
    @DisplayName("Should test InvalidCredentialsException")
    void testInvalidCredentialsException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Bad credentials");
        assertEquals("Bad credentials", ex.getMessage());
    }

    @Test
    @DisplayName("Should test InvalidRequestException")
    void testInvalidRequestException() {
        InvalidRequestException ex = new InvalidRequestException("Invalid request payload");
        assertEquals("Invalid request payload", ex.getMessage());
    }

    @Test
    @DisplayName("Should test PatientNotFoundException")
    void testPatientNotFoundException() {
        PatientNotFoundException ex = new PatientNotFoundException("Patient not found");
        assertEquals("Patient not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should test ResourceNotFoundException")
    void testResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");
        assertEquals("Resource not found", ex.getMessage());
    }

    @Test
    @DisplayName("Should test ResourceValidationException")
    void testResourceValidationException() {
        Map<String, String> errors = Map.of("field", "is required");
        ResourceValidationException ex = new ResourceValidationException(errors);
        assertEquals("Validation Failed", ex.getMessage());
        assertNotNull(ex.getErrors());
        assertEquals("is required", ex.getErrors().get("field"));
    }

    @Test
    @DisplayName("Should test UserNotFoundException")
    void testUserNotFoundException() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        assertEquals("User not found", ex.getMessage());
    }
}
