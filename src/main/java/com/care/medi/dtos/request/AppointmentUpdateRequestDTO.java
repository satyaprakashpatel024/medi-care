package com.care.medi.dtos.request;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentUpdateRequestDTO {
    @Future
    private LocalDateTime appointmentDate;
    @Pattern(regexp = "^(SCHEDULED|CONFIRMED|COMPLETED|CANCELLED|NO_SHOW)$")
    private String status;
    @Size(max = 500)
    private String diagnosis;
    @Size(max = 500)
    private String treatment;
    private String notes;
}
