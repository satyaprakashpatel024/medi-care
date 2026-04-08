package com.care.medi.dtos.request;

import jakarta.validation.constraints.Future;
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
    @Future
    private LocalDateTime appointmentDate;
    @Pattern(regexp = "^(SCHEDULED|COMPLETED|CANCELLED|NO_SHOW)$")
    private String status;
}
