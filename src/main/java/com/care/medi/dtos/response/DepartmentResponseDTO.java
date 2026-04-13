package com.care.medi.dtos.response;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
public record DepartmentResponseDTO(
        Long id,
        String name,
        List<String> hospitalNames,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
