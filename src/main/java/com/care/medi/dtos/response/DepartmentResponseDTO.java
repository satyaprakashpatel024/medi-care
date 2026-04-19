package com.care.medi.dtos.response;

import com.care.medi.entity.Department;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record DepartmentResponseDTO(
        Long id,
        String name,
        String description
) {
    public static DepartmentResponseDTO fromEntity(Department department) {
        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .build();
    }
}
