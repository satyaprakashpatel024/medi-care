package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppointmentListResponseDTO(
        Long appointmentId,
        String patientName,
        String doctorName,
        String departmentName,
        String appointmentDate,
        AppointmentStatus status,
        String appointmentTime
) {

    // Constructor called by JPQL — receives raw ZonedDateTime, formats it
    public AppointmentListResponseDTO(
            Long id,
            String patientName,
            String doctorName,
            String departmentName,
            LocalDate appointmentDate,
            AppointmentStatus status,
            LocalTime appointmentTime
    ) {
        this(
                id,
                patientName,
                doctorName,
                departmentName,
                appointmentDate.format(Constants.HUMAN_DATE_FORMAT),
                status,
                appointmentTime.format(Constants.HUMAN_TIME_FORMAT)
        );
    }

    public static AppointmentListResponseDTO fromEntity(Appointment appointment) {
        return AppointmentListResponseDTO
                .builder()
                .appointmentId(appointment.getId())
                .patientName(String.format("%s %s", appointment.getPatient().getFirstName(), appointment.getPatient().getLastName()))
                .doctorName(String.format("%s %s", appointment.getDoctor().getFirstName(), appointment.getDoctor().getLastName()))
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate().format(Constants.HUMAN_DATE_FORMAT))
                .appointmentTime(appointment.getStartTime().format(Constants.HUMAN_TIME_FORMAT))
                .status(appointment.getStatus())
                .build();
    }
}
