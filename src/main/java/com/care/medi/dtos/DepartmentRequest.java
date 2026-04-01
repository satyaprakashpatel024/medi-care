package com.care.medi.dtos;
import jakarta.validation.constraints.*;
import lombok.*;


@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DepartmentRequest {
    @NotBlank @Size(max = 150)
    private String name;
}