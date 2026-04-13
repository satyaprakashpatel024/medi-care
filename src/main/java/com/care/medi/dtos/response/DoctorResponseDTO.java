package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DoctorResponseDTO(
        Long id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String gender,
        String phone,
        String speciality,
        Long hospitalId,
        String hospitalName,
        Long departmentId,
        String departmentName,
        String emergencyContact,
        String bloodType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<AddressResponseDTO> addresses
) {
}
