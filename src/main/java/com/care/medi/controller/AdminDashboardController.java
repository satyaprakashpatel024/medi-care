package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentAnalyticsResponseDTO;
import com.care.medi.dtos.response.HospitalStatsResponseDTO;
import com.care.medi.dtos.response.SystemKpiResponseDTO;
import com.care.medi.services.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for providing metrics, KPIs, and aggregate analytics for the Admin Dashboard.
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@Validated
public class AdminDashboardController {

    private final AdminAnalyticsService adminAnalyticsService;

    /**
     * Retrieves overall system KPI metrics.
     */
    @GetMapping("/kpis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SystemKpiResponseDTO>> getSystemKpis() {
        return ResponseEntity.ok(
                ApiResponse.success("System KPIs retrieved successfully", adminAnalyticsService.getSystemKpis())
        );
    }

    /**
     * Retrieves hospital-specific utilization statistics and counts.
     */
    @GetMapping("/hospitals/{hospitalId}/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HospitalStatsResponseDTO>> getHospitalStats(
            @PathVariable("hospitalId") Long hospitalId) {
        return ResponseEntity.ok(
                ApiResponse.success("Hospital statistics retrieved successfully", adminAnalyticsService.getHospitalStats(hospitalId))
        );
    }

    /**
     * Retrieves appointment status analytics breakdown.
     */
    @GetMapping("/appointments/analytics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentAnalyticsResponseDTO>> getAppointmentAnalytics(
            @RequestParam(value = "hospitalId", required = false) Long hospitalId) {
        return ResponseEntity.ok(
                ApiResponse.success("Appointment analytics retrieved successfully", adminAnalyticsService.getAppointmentAnalytics(hospitalId))
        );
    }
}
