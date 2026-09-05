package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record HospitalStatsResponseDTO(
        Long hospitalId,
        String hospitalName,
        long activeDoctorsCount,
        long totalPatientsCount,
        long totalStaffCount,
        long appointmentsTodayCount,
        long completedAppointmentsCount
) {
}
