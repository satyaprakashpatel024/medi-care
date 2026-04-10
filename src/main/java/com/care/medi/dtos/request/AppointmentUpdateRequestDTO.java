package com.care.medi.dtos.request;
import jakarta.validation.constraints.*;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentUpdateRequestDTO {
    @Pattern(regexp = "^(SCHEDULED|COMPLETED|CANCELLED|NO_SHOW)$")
    private String status;
    @Size(max = 500)
    private String treatment;
    private String notes;
    @NotNull(message = "Prescription is required.")
    PrescriptionRequestDTO prescription;
}
