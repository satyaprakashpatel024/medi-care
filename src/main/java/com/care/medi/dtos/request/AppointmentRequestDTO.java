package com.care.medi.dtos.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestDTO {
    @NotNull(message = "Patient is required.")
    private PatientRequestDTO patient;
    @NotNull(message = "Doctor is required")
    private Long doctorId;
    @NotNull(message = "Department is required")
    private Long departmentId;
    @NotNull(message = "Hospital is required")
    private Long hospitalId;
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private OffsetDateTime appointmentDate;
}
