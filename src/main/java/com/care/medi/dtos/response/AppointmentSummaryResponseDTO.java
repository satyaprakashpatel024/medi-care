package com.care.medi.dtos.response;

import com.care.medi.entity.AppointmentStatus;
import java.time.ZonedDateTime;

public record AppointmentSummaryResponseDTO(
        Long id,
        ZonedDateTime appointmentDate,
        String patientName,
        String doctorName,
        AppointmentStatus status,
        String departmentName
) {}