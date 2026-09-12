package com.care.medi.security;

import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.care.medi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSecurityTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private InsuranceRepository insuranceRepository;

    @InjectMocks
    private UserSecurity userSecurity;

    private Users userDoctorA;
    private Users userPatientA;
    private Authentication authDoctorA;
    private Authentication authPatientA;

    @BeforeEach
    void setUp() {
        userDoctorA = Users.builder()
                .email("doctorA@hospital.com")
                .role(Role.DOCTOR)
                .build();
        userDoctorA.setId(100L);

        userPatientA = Users.builder()
                .email("patientA@gmail.com")
                .role(Role.PATIENT)
                .build();
        userPatientA.setId(200L);

        authDoctorA = new UsernamePasswordAuthenticationToken(userDoctorA, null, userDoctorA.getAuthorities());
        authPatientA = new UsernamePasswordAuthenticationToken(userPatientA, null, userPatientA.getAuthorities());
    }

    @Test
    @DisplayName("Doctor A requesting Doctor A's info should return true")
    void testIsSelfDoctor_Self_ReturnsTrue() {
        when(doctorRepository.existsByIdAndUserId(1L, 100L)).thenReturn(true);

        boolean result = userSecurity.isSelfDoctor(1L, authDoctorA);

        assertTrue(result);
    }

    @Test
    @DisplayName("Doctor A requesting Doctor B's info should return false")
    void testIsSelfDoctor_OtherDoctor_ReturnsFalse() {
        when(doctorRepository.existsByIdAndUserId(2L, 100L)).thenReturn(false);

        boolean result = userSecurity.isSelfDoctor(2L, authDoctorA);

        assertFalse(result);
    }

    @Test
    @DisplayName("Patient A requesting Patient A's info should return true")
    void testIsSelfPatient_Self_ReturnsTrue() {
        when(patientRepository.existsByIdAndUserId(10L, 200L)).thenReturn(true);

        boolean result = userSecurity.isSelfPatient(10L, authPatientA);

        assertTrue(result);
    }

    @Test
    @DisplayName("Patient A requesting Patient B's info should return false")
    void testIsSelfPatient_OtherPatient_ReturnsFalse() {
        when(patientRepository.existsByIdAndUserId(20L, 200L)).thenReturn(false);

        boolean result = userSecurity.isSelfPatient(20L, authPatientA);

        assertFalse(result);
    }

    @Test
    @DisplayName("Unauthenticated request should return false")
    void testIsSelfDoctor_Unauthenticated_ReturnsFalse() {
        boolean result = userSecurity.isSelfDoctor(1L, null);

        assertFalse(result);
    }

    @Test
    @DisplayName("User ID matching test")
    void testIsSelfUser() {
        assertTrue(userSecurity.isSelfUser(100L, authDoctorA));
        assertFalse(userSecurity.isSelfUser(101L, authDoctorA));
    }
}
