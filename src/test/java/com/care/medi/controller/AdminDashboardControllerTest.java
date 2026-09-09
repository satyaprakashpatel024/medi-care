package com.care.medi.controller;

import com.care.medi.dtos.response.AppointmentAnalyticsResponseDTO;
import com.care.medi.dtos.response.HospitalStatsResponseDTO;
import com.care.medi.dtos.response.SystemKpiResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.AdminAnalyticsService;
import com.care.medi.services.UsersDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminAnalyticsService adminAnalyticsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Test
    @DisplayName("Should retrieve system KPIs successfully")
    void testGetSystemKpis() throws Exception {
        SystemKpiResponseDTO kpis = SystemKpiResponseDTO.builder()
                .totalHospitals(10L)
                .totalDoctors(50L)
                .totalPatients(200L)
                .totalAppointmentsToday(500L)
                .build();

        when(adminAnalyticsService.getSystemKpis()).thenReturn(kpis);

        mockMvc.perform(get("/api/v1/admin/dashboard/kpis")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("System KPIs retrieved successfully"))
                .andExpect(jsonPath("$.data.totalHospitals").value(10))
                .andExpect(jsonPath("$.data.totalDoctors").value(50))
                .andExpect(jsonPath("$.data.totalPatients").value(200))
                .andExpect(jsonPath("$.data.totalAppointmentsToday").value(500));
    }

    @Test
    @DisplayName("Should retrieve hospital statistics successfully")
    void testGetHospitalStats() throws Exception {
        HospitalStatsResponseDTO stats = HospitalStatsResponseDTO.builder()
                .hospitalId(1L)
                .hospitalName("General Hospital")
                .activeDoctorsCount(15L)
                .totalPatientsCount(5L)
                .appointmentsTodayCount(100L)
                .build();

        when(adminAnalyticsService.getHospitalStats(1L)).thenReturn(stats);

        mockMvc.perform(get("/api/v1/admin/dashboard/hospitals/1/stats")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hospital statistics retrieved successfully"))
                .andExpect(jsonPath("$.data.hospitalId").value(1))
                .andExpect(jsonPath("$.data.hospitalName").value("General Hospital"));
    }

    @Test
    @DisplayName("Should retrieve appointment analytics without hospitalId")
    void testGetAppointmentAnalyticsWithoutHospitalId() throws Exception {
        AppointmentAnalyticsResponseDTO analytics = AppointmentAnalyticsResponseDTO.builder()
                .scheduledCount(30L)
                .completedCount(50L)
                .cancelledCount(10L)
                .build();

        when(adminAnalyticsService.getAppointmentAnalytics(null)).thenReturn(analytics);

        mockMvc.perform(get("/api/v1/admin/dashboard/appointments/analytics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment analytics retrieved successfully"))
                .andExpect(jsonPath("$.data.scheduledCount").value(30))
                .andExpect(jsonPath("$.data.completedCount").value(50))
                .andExpect(jsonPath("$.data.cancelledCount").value(10));
    }

    @Test
    @DisplayName("Should retrieve appointment analytics with hospitalId")
    void testGetAppointmentAnalyticsWithHospitalId() throws Exception {
        AppointmentAnalyticsResponseDTO analytics = AppointmentAnalyticsResponseDTO.builder()
                .scheduledCount(10L)
                .completedCount(20L)
                .cancelledCount(2L)
                .build();

        when(adminAnalyticsService.getAppointmentAnalytics(1L)).thenReturn(analytics);

        mockMvc.perform(get("/api/v1/admin/dashboard/appointments/analytics")
                        .param("hospitalId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Appointment analytics retrieved successfully"))
                .andExpect(jsonPath("$.data.scheduledCount").value(10))
                .andExpect(jsonPath("$.data.completedCount").value(20));
    }
}
