package com.care.medi.dtos.request;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentRequestDTO {
    @NotNull(message = "Patient is required.")
    private Long patientId;
    @NotNull(message = "Doctor is required")
    private Long doctorId;
    @NotNull(message = "Department is required")
    private Long departmentId;
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;
}
