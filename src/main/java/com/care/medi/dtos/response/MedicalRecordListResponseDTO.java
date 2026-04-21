package com.care.medi.dtos.response;

import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Lean projection used inside paginated list responses.
 * Omits full clinical text fields to keep list payloads small —
 * callers fetch the full record via GET /medical-records/{id} when needed.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordListResponseDTO {

    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String doctorSpeciality;
    private Long appointmentId;
    private String diagnosis;           // truncated to 120 chars in mapper
    private LocalDate recordDate;
    private String status;
    private ZonedDateTime createdAt;
}