package com.care.medi.dtos.response;

import com.care.medi.entity.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppointmentListResponseDTO(
        Long id,
        String patientName,
        String doctorName,
        String departmentName,
        LocalDateTime appointmentDate,
        AppointmentStatus status
) {
}
