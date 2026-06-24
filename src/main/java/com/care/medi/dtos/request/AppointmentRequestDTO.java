package com.care.medi.dtos.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @NotNull(message = "Appointment date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String appointmentDate;
    @NotNull(message = "Appointment time is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a")
    private String appointmentTime;
}
