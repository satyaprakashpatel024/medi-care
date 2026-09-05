package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateRequestDTO {
    @NotNull(message = "isActive flag is required")
    private Boolean isActive;
}
