package com.care.medi.dtos.request;
import jakarta.validation.constraints.*;
import lombok.*;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DepartmentRequestDTO {
    @NotBlank(message = "Department Name is required.")
    @Size(max = 150)
    private String name;
}