package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record SystemKpiResponseDTO(
        long totalHospitals,
        long totalDoctors,
        long totalPatients,
        long totalStaff,
        long totalAppointmentsToday,
        long totalDepartments
) {
}
