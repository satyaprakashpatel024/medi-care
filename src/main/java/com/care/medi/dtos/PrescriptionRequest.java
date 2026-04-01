package com.care.medi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequest {
    @NotNull
    private Integer patientId;
    @NotNull
    private Integer doctorId;
    @NotBlank
    private String medications;
    private String dosageInstructions;
    private String notes;
}
