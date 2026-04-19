package com.care.medi.dtos.response;

import com.care.medi.entity.Patient;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatientListResponseDTO(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String emergencyContact,
        String bloodGroup,
        ZonedDateTime createdAt
) {
    public static PatientListResponseDTO fromEntity(Patient patient) {
        return PatientListResponseDTO
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
                .createdAt(patient.getCreatedAt())
                .build();
    }
}
