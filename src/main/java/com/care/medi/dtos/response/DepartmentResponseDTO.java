package com.care.medi.dtos.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record DepartmentResponseDTO (
        Long id,
        String name,
        List<String> hospitalNames,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){ }
