package com.care.medi.dtos.request;
import com.care.medi.entity.Prescription;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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
