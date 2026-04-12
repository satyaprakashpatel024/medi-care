package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentUpdateRequestDTO {
    @NotNull(message = "Prescription is required.")
    PrescriptionRequestDTO prescription;
    @Pattern(regexp = "^(SCHEDULED|COMPLETED|CANCELLED|NO_SHOW)$")
    private String status;
    @Size(max = 500)
    private String treatment;
    private String notes;
}
