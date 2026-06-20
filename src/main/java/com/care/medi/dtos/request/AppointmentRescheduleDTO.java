package com.care.medi.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String appointmentDate;
    @NotNull(message = "Appointment time is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private String appointmentTime;
    @NotNull(message = "Status is required.")
    @Pattern(regexp = "^(SCHEDULED|CANCELLED|NO_SHOW)$")
    private String status;
}
