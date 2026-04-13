package com.care.medi.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email")  // remove unique=true from here
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
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
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.GUEST;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    // Automatically updates whenever the entity is modified
    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime updatedAt;

    @Column(name = "last_login", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime lastLogin;

    /**
     * Logic-based constraints:
     * This ensures that when a user is first created, lastLogin is null
     * until their first successful authentication.
     */
    public void markLogin() {
        this.lastLogin = OffsetDateTime.now();
    }
}