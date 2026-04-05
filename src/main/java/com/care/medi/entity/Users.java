package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true)
})
@Schema(hidden = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(PATIENT|DOCTOR|STAFF|ADMIN|RECEPTIONIST)$", message = "Role must be one of: PATIENT, DOCTOR, STAFF, ADMIN, RECEPTIONIST")
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    //    --- for Bidirectional Mapping
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Address> addresses = new LinkedHashSet<>();
}