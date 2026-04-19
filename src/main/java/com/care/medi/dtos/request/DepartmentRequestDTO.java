package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentRequestDTO {
    @NotNull(message = "Description is required.")
    String description;
    @NotBlank(message = "Department Name is required.")
    @Size(max = 150)
    private String name;

}