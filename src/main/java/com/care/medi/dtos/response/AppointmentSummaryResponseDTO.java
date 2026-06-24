package com.care.medi.dtos.response;

import com.care.medi.entity.AppointmentStatus;
import com.care.medi.utils.Constants;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentSummaryResponseDTO(
        Long appointmentId,
        String appointmentDate,
        String patientName,
        String doctorName,
        AppointmentStatus status,
        String departmentName,
        String appointmentTime
) {
    // Constructor called by JPQL — receives raw ZonedDateTime, formats it
    public AppointmentSummaryResponseDTO(
            Long id,
            LocalDate appointmentDate,
            String patientName,
            String doctorName,
            AppointmentStatus status,
            String departmentName,
            LocalTime appointmentTime
    ) {
        this(
                id,
                appointmentDate.format(Constants.HUMAN_DATE_FORMAT),
                patientName,
                doctorName,
                status,
                departmentName,
                appointmentTime.format(Constants.HUMAN_TIME_FORMAT)
        );
    }
}