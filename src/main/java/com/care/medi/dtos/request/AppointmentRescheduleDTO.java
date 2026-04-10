package com.care.medi.dtos.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRescheduleDTO {
    @Future(message = "Appointment date must be in the future.")
    @NotNull(message = "Appointment date is required.")
    private LocalDateTime appointmentDate;
    @NotNull(message = "Status is required.")
    @Pattern(regexp = "^(SCHEDULED|CANCELLED|NO_SHOW)$")
    private String status;
}
