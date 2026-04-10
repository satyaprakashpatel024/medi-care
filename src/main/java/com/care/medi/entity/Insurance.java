package com.care.medi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "insurances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Insurance provider name is required")
    @Size(min = 2, max = 100, message = "Provider name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String providerName;

    @NotBlank(message = "Policy number is required")
    @Column(unique = true, nullable = false)
    private String policyNumber;

    @NotBlank(message = "Policy type is required (e.g., Health, Life, Dental)")
    private String policyType;

    // Financial Fields
    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage must be greater than 0")
    private Double coverageAmount;

    @DecimalMin(value = "0.0", message = "Deductible cannot be negative")
    private Double deductible;

    // Date Validations
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;

    @Email(message = "Invalid provider contact email")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Email format is invalid")
    private String providerContactEmail;

    @Pattern(regexp = "^(?:(?:\\+|00)91[\\-\\s]?)?[6-9]\\d{9}$",
            message = "Invalid phone number, Please provide valid Indian Phone number.")
    private String providerPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false,name = "status")
    private InsuranceStatus status = InsuranceStatus.ACTIVE;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // Helper method to check if the policy is currently valid
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}