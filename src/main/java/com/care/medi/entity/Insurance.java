package com.care.medi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "insurances",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_insurances_policy_number", columnNames = "policy_number")
        }
)
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
    @Column(name = "provider_name", nullable = false)
    private String providerName;

    @NotBlank(message = "Policy number is required")
    @Column(name = "policy_number", nullable = false)
    private String policyNumber;

    @NotBlank(message = "Policy type is required (e.g., Health, Life, Dental)")
    @Column(name = "policy_type", nullable = false)
    private String policyType;

    // Financial Fields
    @NotNull(message = "Coverage amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Coverage must be greater than 0")
    @Column(name = "coverage_amount", nullable = false)
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
    @Column(nullable = false, name = "status")
    private InsuranceStatus status = InsuranceStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", foreignKey = @ForeignKey(name = "fk_insurance_patient"))
    private Patient patient;

    // Helper method to check if the policy is currently valid
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}