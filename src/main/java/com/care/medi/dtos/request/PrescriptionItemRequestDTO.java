package com.care.medi.dtos.request;

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
public class PrescriptionItemRequestDTO implements java.io.Serializable {

    @NotNull(message = "Medication ID is required.")
    private Long medicationId;

    @NotBlank(message = "Dosage instructions are required.")
    private String dosageInstructions;

    private String notes;
}
