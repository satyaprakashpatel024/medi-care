package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
        String bloodType,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
