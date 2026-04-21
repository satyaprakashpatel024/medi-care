package com.care.medi.dtos.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordUpdateRequestDTO {

    @Size(min = 5, max = 1000, message = "Diagnosis must be between 5 and 1000 characters")
    private String diagnosis;

    @Size(max = 1000, message = "Symptoms must not exceed 1000 characters")
    private String symptoms;

    @Size(max = 2000, message = "Treatment plan must not exceed 2000 characters")
    private String treatmentPlan;

    @Size(max = 1000, message = "Follow-up notes must not exceed 1000 characters")
    private String followUpNotes;

    @PastOrPresent(message = "Record date cannot be in the future")
    private LocalDate recordDate;

    /**
     * ACTIVE or ARCHIVED.
     * Passing ARCHIVED here is the preferred way to supersede a record
     * instead of deleting it.
     */
    @Pattern(
            regexp = "^(ACTIVE|ARCHIVED)$",
            message = "Status must be ACTIVE or ARCHIVED"
    )
    private String status;
}