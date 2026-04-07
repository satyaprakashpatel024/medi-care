package com.care.medi.dtos.response;

import com.care.medi.entity.Appointment;
import com.care.medi.entity.Prescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;

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
        LocalDateTime appointmentDate,
        String status,
        String diagnosis,
        String treatment,
        String notes,
        LocalDateTime createdAt) {


    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(STR."\{appointment.getPatient().getFirstName()} \{appointment.getPatient().getLastName()}")
                .doctorId(appointment.getDoctor().getId())
                .doctorName(STR."\{appointment.getDoctor().getFirstName()} \{appointment.getDoctor().getLastName()}")
                .departmentId(appointment.getDepartment().getId())
                .departmentName(appointment.getDepartment().getName())
                .prescriptionId(appointment.getPrescription().getId())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus().name())
                .diagnosis(appointment.getDiagnosis())
                .treatment(appointment.getTreatment())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }

}