package com.care.medi.dtos.response;

import com.care.medi.entity.Patient;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatientResponseDTO(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String emergencyContact,
        String bloodGroup,
        ZonedDateTime updatedAt,
        Set<AppointmentResponseDTO> appointments
) {
    // ── Helper methods ──────────────────────────────────────────────
    public static PatientResponseDTO fromEntity(Patient patient) {
        return PatientResponseDTO
                .builder()
                .id(patient.getId())
                .bloodGroup(patient.getBloodGroup().toString())
                .gender(patient.getGender().toString())
                .phone(patient.getPhone())
                .emergencyContact(patient.getEmergencyContact())
                .dateOfBirth(patient.getDateOfBirth())
                .userId(patient.getUser().getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .updatedAt(patient.getUpdatedAt())
                .appointments(AppointmentResponseDTO.fromEntity(patient.getAppointments()))
                .build();
    }

}
