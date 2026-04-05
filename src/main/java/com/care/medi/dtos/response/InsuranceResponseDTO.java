package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import java.time.LocalDate;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InsuranceResponseDTO(
        Long id,
        String providerName,
        String policyNumber,
        String policyType,
        Double coverageAmount,
        Double deductible,
        LocalDate startDate,
        LocalDate expiryDate,
        String networkType,
        String status,
        String providerContactEmail,
        String providerPhoneNumber
) {}