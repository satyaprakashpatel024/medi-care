package com.care.medi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tables", indexes = {
        @Index(name = "idx_otp_email", columnList = "email"),
        @Index(name = "idx_otp_expired_at", columnList = "expired_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OtpTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 10, message = "OTP must be between 4 and 10 characters")
    @Column(nullable = false, length = 10)
    private String otp;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Expiry time is required")
    @Future(message = "Expiry time must be in the future")
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}