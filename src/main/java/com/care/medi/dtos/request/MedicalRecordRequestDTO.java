package com.care.medi.dtos.request;


import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequestDTO {

    @NotNull(message = "Patient ID is required")
    @Min(value = 1, message = "Patient ID must be a positive number")
    private Long patientId;

    @NotNull(message = "Doctor ID is required")
    @Min(value = 1, message = "Doctor ID must be a positive number")
    private Long doctorId;

    /**
     * Links this record to an existing appointment.
     * Optional — omit for retrospective / imported records.
     */
    @Min(value = 1, message = "Appointment ID must be a positive number")
    private Long appointmentId;

    @NotBlank(message = "Diagnosis is required")
    @Size(min = 5, max = 1000, message = "Diagnosis must be between 5 and 1000 characters")
    private String diagnosis;

    @Size(max = 1000, message = "Symptoms must not exceed 1000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Treatment plan must not exceed 2000 characters")
    private String treatmentPlan;

    @Size(max = 1000, message = "Follow-up notes must not exceed 1000 characters")
    private String followUpNotes;

    @NotNull(message = "Record date is required")
    @PastOrPresent(message = "Record date cannot be in the future")
    private LocalDate recordDate;
}