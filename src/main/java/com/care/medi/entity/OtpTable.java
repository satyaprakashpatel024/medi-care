package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "otp_tables", indexes = {
        @Index(name = "idx_otp_email", columnList = "email"),
        @Index(name = "idx_otp_expired_at", columnList = "expired_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(hidden = true)
public class OtpTable extends BaseEntity {

    @NotBlank(message = "OTP is required")
    @Size(min = 4, max = 10, message = "OTP must be between 4 and 10 characters")
    @Column(nullable = false, length = 10)
    private String otp;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false)
    private String email;

    @NotNull(message = "Expiry time is required")
    @Column(name = "expired_at", nullable = false)
    private ZonedDateTime expiredAt;

    @PrePersist
    public void prePersist() {
        this.setCreatedAt(ZonedDateTime.now());
        this.expiredAt = this.getCreatedAt().plusMinutes(5);
    }
}