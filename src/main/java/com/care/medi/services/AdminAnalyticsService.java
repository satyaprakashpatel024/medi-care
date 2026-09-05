package com.care.medi.services;

import com.care.medi.dtos.response.AppointmentAnalyticsResponseDTO;
import com.care.medi.dtos.response.HospitalStatsResponseDTO;
import com.care.medi.dtos.response.SystemKpiResponseDTO;

public interface AdminAnalyticsService {
    SystemKpiResponseDTO getSystemKpis();

    HospitalStatsResponseDTO getHospitalStats(Long hospitalId);

    AppointmentAnalyticsResponseDTO getAppointmentAnalytics(Long hospitalId);
}
