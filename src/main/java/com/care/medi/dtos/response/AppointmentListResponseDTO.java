package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppointmentListResponseDTO(
        Long appointmentId,
        String patientName,
        String doctorName,
        String departmentName,
        String appointmentDate,
        AppointmentStatus status
) {

    // Constructor called by JPQL — receives raw ZonedDateTime, formats it
    public AppointmentListResponseDTO(
            Long id,
            String patientName,
            String doctorName,
            String departmentName,
            ZonedDateTime appointmentDate,
            AppointmentStatus status) {
        this(
                id,
                patientName,
                doctorName,
                departmentName,
                appointmentDate.withZoneSameInstant(Constants.ZONE_ID)
                        .format(Constants.HUMAN_DATETIME_FORMAT),
                status
        );
    }

    public static AppointmentListResponseDTO fromEntity(Appointment appointment) {
        ZonedDateTime zonedDateTime = appointment.getAppointmentDate().withZoneSameInstant(Constants.ZONE_ID);
        return AppointmentListResponseDTO
                .builder()
                .appointmentId(appointment.getId())
                .patientName(STR."\{appointment.getPatient().getFirstName()} \{appointment.getPatient().getLastName()}")
                .doctorName(STR."\{appointment.getDoctor().getFirstName()} \{appointment.getDoctor().getLastName()}")
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(zonedDateTime.format(Constants.HUMAN_DATETIME_FORMAT))
                .status(appointment.getStatus())
                .build();
    }
}
