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
        private String providerName;

        @NotBlank(message = "Policy number is required")
        private String policyNumber;

        @NotBlank(message = "Policy type is required")
        private String policyType;

        @NotNull(message = "Coverage amount is required")
        @Positive(message = "Coverage must be greater than zero")
        private Double coverageAmount;

        @PositiveOrZero(message = "Deductible cannot be negative")
        private Double deductible;

        @NotNull(message = "Insurance status is required")
        @Pattern(regexp = "^(?:ACTIVE|EXPIRED|CANCELLED|PENDING_VERIFICATION)$", message = "Invalid insurance status")
        private String insuranceStatus;

        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "Expiry date is required")
        @Future(message = "Expiry date must be in the future")
        private LocalDate expiryDate;

        @NotNull(message = "Provider contact email is required")
        @Email(message = "Invalid email format")
        private String providerContactEmail;

        @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
                message = "Invalid phone number, Please provide valid Indian Phone number.")
        private String providerPhoneNumber;

}