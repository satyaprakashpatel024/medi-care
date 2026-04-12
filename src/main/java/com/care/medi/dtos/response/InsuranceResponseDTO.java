package com.care.medi.dtos.response;

import com.care.medi.entity.Insurance;
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
        String status,
        String providerContactEmail,
        String providerPhoneNumber
) {
    public static InsuranceResponseDTO fromEntity(Insurance insurance) {
        return InsuranceResponseDTO.builder()
                .id(insurance.getId())
                .providerName(insurance.getProviderName())
                .policyNumber(insurance.getPolicyNumber())
                .coverageAmount(insurance.getCoverageAmount())
                .deductible(insurance.getDeductible())
                .startDate(insurance.getStartDate())
                .expiryDate(insurance.getExpiryDate())
                .status(insurance.getStatus().toString())
                .providerContactEmail(insurance.getProviderContactEmail())
                .providerPhoneNumber(insurance.getProviderPhoneNumber())
                .build();
    }
}