package com.care.medi.dtos.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsuranceRequestDTO {
        @NotBlank(message = "Provider name cannot be empty")
        String providerName;

        @NotBlank(message = "Policy number is required")
        String policyNumber;

        @NotBlank(message = "Policy type is required")
        String policyType;

        @NotNull(message = "Coverage amount is required")
        @Positive(message = "Coverage must be greater than zero")
        Double coverageAmount;

        @PositiveOrZero(message = "Deductible cannot be negative")
        Double deductible;

        @NotNull(message = "Start date is required")
        LocalDate startDate;

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate;

        @NotBlank(message = "Network type (HMO/PPO) is required")
        String networkType;

        @Email(message = "Invalid email format")
        String providerContactEmail;

        @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,15}$", message = "Invalid phone format")
        String providerPhoneNumber;

}