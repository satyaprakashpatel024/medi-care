package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentRescheduleDTO {
    @NotNull(message = "Appointment date is required.")
    private String appointmentDate;
    @NotNull(message = "Status is required.")
    @Pattern(regexp = "^(SCHEDULED|CANCELLED|NO_SHOW)$")
    private String status;
}
