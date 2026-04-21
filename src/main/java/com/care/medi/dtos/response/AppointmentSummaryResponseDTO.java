package com.care.medi.dtos.response;

import com.care.medi.entity.AppointmentStatus;
import com.care.medi.utils.Constants;

import java.time.ZonedDateTime;

public record AppointmentSummaryResponseDTO(
        Long appointmentId,
        String appointmentDate,
        String patientName,
        String doctorName,
        AppointmentStatus status,
        String departmentName
) {
    // Constructor called by JPQL — receives raw ZonedDateTime, formats it
    public AppointmentSummaryResponseDTO(
            Long id,
            ZonedDateTime appointmentDate,
            String patientName,
            String doctorName,
            AppointmentStatus status,
            String departmentName
    ) {
        this(
                id,
                appointmentDate.withZoneSameInstant(Constants.ZONE_ID)
                        .format(Constants.HUMAN_DATETIME_FORMAT),
                patientName,
                doctorName,
                status,
                departmentName

        );
    }
}