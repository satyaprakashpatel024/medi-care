package com.care.medi.dtos;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionResponse {
    private Integer id;
    private Integer patientId;
    private String patientName;
    private Integer doctorId;
    private String doctorName;
    private String medications;
    private String dosageInstructions;
    private String notes;
    private LocalDateTime createdAt;
}
