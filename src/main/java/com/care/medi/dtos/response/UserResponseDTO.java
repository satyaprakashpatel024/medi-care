package com.care.medi.dtos.response;

import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record UserResponseDTO(
        Long id,
        String email,
        Role role,
        Boolean isActive,
        OffsetDateTime lastLogin,
        OffsetDateTime createdAt
) {
    public static UserResponseDTO fromEntity(Users user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toOffsetDateTime() : null)
                .build();
    }
}
