package com.care.medi.utils;

import com.care.medi.entity.Patient;
import com.care.medi.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HelpersTest {

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        Helpers helpers = new Helpers();
        helpers.setDevEmail("1008tonystark@gmail.com");
        helpers.setIsDevEnvironment(true);
    }

    @Test
    @DisplayName("Should get start of the day")
    void testGetStartOfTheDay() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        LocalDate start = Helpers.getStartOfTheDay(date);
        assertEquals(date, start);
    }

    @Test
    @DisplayName("Should get end of the day")
    void testGetEndOfTheDay() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        LocalDate end = Helpers.getEndOfTheDay(date);
        assertEquals(date, end);
    }

    @Test
    @DisplayName("Should parse valid appointment date")
    void testParseAppointmentDateValid() {
        Map<String, String> errorMap = new HashMap<>();
        LocalDate date = Helpers.parseAppointmentDate("2025-01-01", errorMap);
        assertEquals(LocalDate.of(2025, 1, 1), date);
        assertTrue(errorMap.isEmpty());
    }

    @Test
    @DisplayName("Should handle null or blank appointment date")
    void testParseAppointmentDateNullOrBlank() {
        Map<String, String> errorMap = new HashMap<>();
        assertNull(Helpers.parseAppointmentDate(null, errorMap));
        assertNull(Helpers.parseAppointmentDate("  ", errorMap));
    }

    @Test
    @DisplayName("Should handle invalid appointment date")
    void testParseAppointmentDateInvalid() {
        Map<String, String> errorMap = new HashMap<>();
        LocalDate date = Helpers.parseAppointmentDate("01-01-2025", errorMap);
        assertNull(date);
        assertEquals("Invalid date format. Expected: yyyy-MM-dd (e.g., 2026-04-17)", errorMap.get("appointmentDate"));
    }

    @Test
    @DisplayName("Should parse valid appointment time")
    void testParseAppointmentTimeValid() {
        Map<String, String> errorMap = new HashMap<>();
        LocalTime time = Helpers.parseAppointmentTime("10:00 AM", errorMap);
        assertEquals(LocalTime.of(10, 0), time);
        assertTrue(errorMap.isEmpty());
    }

    @Test
    @DisplayName("Should handle null or blank appointment time")
    void testParseAppointmentTimeNullOrBlank() {
        Map<String, String> errorMap = new HashMap<>();
        assertNull(Helpers.parseAppointmentTime(null, errorMap));
        assertEquals("Time is required", errorMap.get("appointmentTime"));

        errorMap.clear();
        assertNull(Helpers.parseAppointmentTime("  ", errorMap));
        assertEquals("Time is required", errorMap.get("appointmentTime"));
    }

    @Test
    @DisplayName("Should handle invalid appointment time")
    void testParseAppointmentTimeInvalid() {
        Map<String, String> errorMap = new HashMap<>();
        assertNull(Helpers.parseAppointmentTime("10:00", errorMap));
        assertEquals("Invalid format. Expected: 10:00 AM", errorMap.get("appointmentTime"));
    }

    @Test
    @DisplayName("Should get recipient email for string")
    void testGetRecipientEmailString() {
        // Based on isDevEnvironment=true in Helpers
        String email = Helpers.getRecipientEmail("realuser@example.com");
        assertEquals("1008tonystark@gmail.com", email);
    }

    @Test
    @DisplayName("Should get recipient email for patient")
    void testGetRecipientEmailPatient() {
        Users user = new Users();
        user.setEmail("realuser@example.com");
        Patient patient = new Patient();
        patient.setUser(user);

        String email = Helpers.getRecipientEmail(patient);
        assertEquals("1008tonystark@gmail.com", email);
    }

    @Test
    @DisplayName("Should get recipient email for null patient")
    void testGetRecipientEmailNullPatient() {
        String email = Helpers.getRecipientEmail((Patient) null);
        assertEquals("1008tonystark@gmail.com", email);
    }
}
