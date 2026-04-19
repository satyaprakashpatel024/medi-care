package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record AddressResponseDTO(
        Long id,
        Long userId,
        String phoneNumber,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String postalCode,
        String country,
        String landmark,
        String addressType,
        Boolean isDefault,
        ZonedDateTime updatedAt
) {
}

