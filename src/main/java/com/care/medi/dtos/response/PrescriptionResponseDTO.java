package com.care.medi.dtos.response;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionResponseDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String medications;
    private String dosageInstructions;
    private String notes;
    private LocalDateTime createdAt;
}
