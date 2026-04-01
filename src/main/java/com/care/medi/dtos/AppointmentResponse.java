package com.care.medi.dtos;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AppointmentResponse {
    private Integer id;
    private Integer patientId;
    private String patientName;
    private Integer doctorId;
    private String doctorName;
    private Integer departmentId;
    private String departmentName;
    private Integer prescriptionId;
    private LocalDateTime appointmentDate;
    private String status;
    private String diagnosis;
    private String treatment;
    private String notes;
    private LocalDateTime createdAt;
}