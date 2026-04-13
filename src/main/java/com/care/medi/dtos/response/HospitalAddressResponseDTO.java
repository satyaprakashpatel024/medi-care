package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record HospitalAddressResponseDTO(
        Long id,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        String landmark,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
