package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
import com.care.medi.utils.Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AppointmentResponseDTO(
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long departmentId,
        String departmentName,
        String appointmentDate,
        String appointmentTime,
        String status,
        String treatment,
        String notes,
        String updatedAt) {


    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(String.format("%s %s", appointment.getPatient().getFirstName(), appointment.getPatient().getLastName()))
                .doctorName(String.format("%s %s", appointment.getDoctor().getFirstName(), appointment.getDoctor().getLastName()))
                .doctorId(appointment.getDoctor().getId())
                .departmentId(appointment.getDepartment().getId())
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate().format(Constants.HUMAN_DATE_FORMAT))
                .appointmentTime(appointment.getStartTime().format(Constants.HUMAN_TIME_FORMAT))
                .status(appointment.getStatus().name())
                .treatment(appointment.getTreatment())
                .notes(appointment.getNotes())
                .updatedAt(appointment.getUpdatedAt().format(Constants.HUMAN_DATETIME_FORMAT))
                .build();
    }

    public static AppointmentResponseDTO toResponse(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .appointmentId(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(String.format("%s %s", appointment.getDoctor().getFirstName(), appointment.getDoctor().getLastName()))
                .departmentId(appointment.getDepartment().getId())
                .departmentName(appointment.getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate().format(Constants.HUMAN_DATE_FORMAT))
                .appointmentTime(appointment.getStartTime().format(Constants.HUMAN_TIME_FORMAT))
                .status(appointment.getStatus().name())
                .treatment(appointment.getTreatment())
                .notes(appointment.getNotes())
                .build();
    }

    public static Set<AppointmentResponseDTO> fromEntity(Set<Appointment> appointment) {
        return appointment.stream().map(AppointmentResponseDTO::toResponse).collect(Collectors.toSet());
    }
}