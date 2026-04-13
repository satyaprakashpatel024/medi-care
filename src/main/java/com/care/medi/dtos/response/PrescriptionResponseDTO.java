package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PrescriptionResponseDTO(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String medications,
        String dosageInstructions,
        String notes,
        OffsetDateTime createdAt
) {
}
