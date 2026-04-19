package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequestDTO {
    @NotBlank(message = "Medications is required.")
    private String medications;
    @NotBlank(message = "Dosage instructions is required.")
    private String dosageInstructions;
    private String notes;
}
