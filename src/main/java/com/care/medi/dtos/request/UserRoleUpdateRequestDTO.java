package com.care.medi.dtos.request;

import com.care.medi.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateRequestDTO {
    @NotNull(message = "Role is required")
    private Role role;
}
