package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequestDTO {
    @NotNull(message = "Patient ID is required.")
    private  Long patientId;
    @NotNull(message = "Appointment ID is required.")
    private Long appointmentId;
    @NotNull(message = "Doctor ID is required.")
    private Long doctorId;
    @NotBlank(message = "Medications is required.")
    private String medications;
    @NotBlank(message = "Dosage instructions is required.")
    private String dosageInstructions;
    private String notes;
}
