package com.care.medi.services;

import com.care.medi.dtos.response.AppointmentAnalyticsResponseDTO;
import com.care.medi.dtos.response.HospitalStatsResponseDTO;
import com.care.medi.dtos.response.SystemKpiResponseDTO;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.entity.Hospital;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminAnalyticsService Unit Tests")
class AdminAnalyticsServiceImplTest {

    @Mock
    private HospitalRepository hospitalRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private StaffRepository staffRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private AdminAnalyticsServiceImpl adminAnalyticsService;

    private Hospital testHospital;

    @BeforeEach
    void setUp() {
        testHospital = new Hospital();
        testHospital.setId(1L);
        testHospital.setName("Central Hospital");
    }

    @Test
    @DisplayName("Should return system KPIs correctly")
    void testGetSystemKpis() {
        when(hospitalRepository.count()).thenReturn(10L);
        when(doctorRepository.countByIsActiveTrue()).thenReturn(50L);
        when(patientRepository.count()).thenReturn(100L);
        when(staffRepository.count()).thenReturn(200L);
        when(appointmentRepository.countByAppointmentDate(any())).thenReturn(30L);
        when(departmentRepository.count()).thenReturn(5L);

        SystemKpiResponseDTO kpis = adminAnalyticsService.getSystemKpis();

        assertNotNull(kpis);
        assertEquals(10L, kpis.totalHospitals());
        assertEquals(50L, kpis.totalDoctors());
        assertEquals(100L, kpis.totalPatients());
        assertEquals(200L, kpis.totalStaff());
        assertEquals(30L, kpis.totalAppointmentsToday());
        assertEquals(5L, kpis.totalDepartments());
    }

    @Test
    @DisplayName("Should return hospital stats correctly")
    void testGetHospitalStats() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(testHospital));
        when(doctorRepository.countByHospitalIdAndIsActiveTrue(1L)).thenReturn(10L);
        when(patientRepository.countByHospitalId(1L)).thenReturn(50L);
        when(staffRepository.countByHospitalId(1L)).thenReturn(20L);
        when(appointmentRepository.countByHospitalIdAndAppointmentDate(eq(1L), any())).thenReturn(15L);
        when(appointmentRepository.countByHospitalIdAndStatus(1L, AppointmentStatus.COMPLETED)).thenReturn(5L);

        HospitalStatsResponseDTO stats = adminAnalyticsService.getHospitalStats(1L);

        assertNotNull(stats);
        assertEquals(1L, stats.hospitalId());
        assertEquals("Central Hospital", stats.hospitalName());
        assertEquals(10L, stats.activeDoctorsCount());
        assertEquals(50L, stats.totalPatientsCount());
        assertEquals(20L, stats.totalStaffCount());
        assertEquals(15L, stats.appointmentsTodayCount());
        assertEquals(5L, stats.completedAppointmentsCount());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid hospital ID in getHospitalStats")
    void testGetHospitalStats_NotFound() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminAnalyticsService.getHospitalStats(1L));
        verify(hospitalRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return appointment analytics with hospital ID")
    void testGetAppointmentAnalytics_WithHospitalId() {
        when(hospitalRepository.existsById(1L)).thenReturn(true);
        when(appointmentRepository.countByHospitalId(1L)).thenReturn(100L);
        when(appointmentRepository.countByHospitalIdAndStatus(1L, AppointmentStatus.SCHEDULED)).thenReturn(40L); // it checks scheduled twice for scheduled and confirmed
        when(appointmentRepository.countByHospitalIdAndStatus(1L, AppointmentStatus.COMPLETED)).thenReturn(50L);
        when(appointmentRepository.countByHospitalIdAndStatus(1L, AppointmentStatus.CANCELLED)).thenReturn(10L);

        AppointmentAnalyticsResponseDTO analytics = adminAnalyticsService.getAppointmentAnalytics(1L);

        assertNotNull(analytics);
        assertEquals(1L, analytics.hospitalId());
        assertEquals(100L, analytics.totalAppointments());
        assertEquals(40L, analytics.scheduledCount());
        assertEquals(40L, analytics.confirmedCount()); // Code in service sets scheduled count to confirmed count as well
        assertEquals(50L, analytics.completedCount());
        assertEquals(10L, analytics.cancelledCount());
    }

    @Test
    @DisplayName("Should return appointment analytics without hospital ID")
    void testGetAppointmentAnalytics_WithoutHospitalId() {
        when(appointmentRepository.count()).thenReturn(500L);

        AppointmentAnalyticsResponseDTO analytics = adminAnalyticsService.getAppointmentAnalytics(null);

        assertNotNull(analytics);
        assertNull(analytics.hospitalId());
        assertEquals(500L, analytics.totalAppointments());
        assertEquals(0L, analytics.scheduledCount()); // Returns 0 if hospitalId is null in service
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid hospital ID in getAppointmentAnalytics")
    void testGetAppointmentAnalytics_NotFound() {
        when(hospitalRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminAnalyticsService.getAppointmentAnalytics(1L));
        verify(hospitalRepository).existsById(1L);
    }
}
