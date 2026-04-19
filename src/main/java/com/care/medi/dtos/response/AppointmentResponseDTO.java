package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AppointmentResponseDTO(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long departmentId,
        String departmentName,
        Long prescriptionId,
        String appointmentDate,
        String status,
        String treatment,
        String notes,
        ZonedDateTime updatedAt) {


    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        ZonedDateTime zonedDateTime = appointment.getAppointmentDate().withZoneSameInstant(Constants.ZONE_ID);
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(STR."\{appointment.getPatient().getFirstName()} \{appointment.getPatient().getLastName()}")
                .doctorId(appointment.getDoctor().getId())
                .doctorName(STR."\{appointment.getDoctor().getFirstName()} \{appointment.getDoctor().getLastName()}")
                .departmentId(appointment.getDepartment().getId())
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(zonedDateTime.format(Constants.HUMAN_DATETIME_FORMAT))
                .status(appointment.getStatus().name())
                .treatment(appointment.getTreatment())
                .notes(appointment.getNotes())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    public static Set<AppointmentResponseDTO> fromEntity(Set<Appointment> appointment) {
        return appointment.stream().map(AppointmentResponseDTO::fromEntity).collect(Collectors.toSet());
    }
}