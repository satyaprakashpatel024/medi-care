package com.care.medi.dtos;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentRequest {
    @NotNull
    private Integer patientId;
    @NotNull
    private Integer doctorId;
    private Integer departmentId;
    @NotNull @Future
    private LocalDateTime appointmentDate;
    @NotBlank @Pattern(regexp = "^(SCHEDULED|CONFIRMED|COMPLETED|CANCELLED|NO_SHOW)$")
    private String status;
    @Size(max = 500)
    private String diagnosis;
    @Size(max = 500)
    private String treatment;
    private String notes;
}
