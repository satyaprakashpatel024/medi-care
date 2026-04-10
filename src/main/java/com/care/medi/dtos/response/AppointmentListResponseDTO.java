package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
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

    public static AppointmentListResponseDTO fromEntity(Appointment appointment) {
        return AppointmentListResponseDTO
                .builder()
                .id(appointment.getId())
                .patientName(STR."\{appointment.getPatient().getFirstName()} \{appointment.getPatient().getLastName()}")
                .doctorName(STR."\{appointment.getDoctor().getFirstName()} \{appointment.getDoctor().getLastName()}")
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .build();
    }
}
