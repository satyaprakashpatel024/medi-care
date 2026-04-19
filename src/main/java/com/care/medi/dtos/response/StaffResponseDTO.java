package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record StaffResponseDTO(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        Long hospitalId,
        String hospitalName,
        String emergencyContact,
        String bloodType,
        ZonedDateTime updatedAt
) {
}
