package com.care.medi.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponseDTO {

    private Long id;

    // Patient summary
    private Long patientId;
    private String patientName;
    private String patientBloodGroup;
    private LocalDate patientDateOfBirth;

    // Doctor summary
    private Long doctorId;
    private String doctorName;
    private String doctorSpeciality;
    private Long departmentId;
    private String departmentName;

    // Appointment link (null when not linked)
    private Long appointmentId;
    private String appointmentDate;

    // Hospital context
    private Long hospitalId;
    private String hospitalName;

    // Clinical content
    private String diagnosis;
    private String symptoms;
    private String treatmentPlan;
    private String followUpNotes;

    private LocalDate recordDate;
    private String status;

    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
