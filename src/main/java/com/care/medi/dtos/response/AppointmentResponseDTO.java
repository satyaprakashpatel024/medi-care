package com.care.medi.dtos.response;
import lombok.*;

import java.time.LocalDateTime;

@Builder 
public record AppointmentResponseDTO(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        Long departmentId,
        String departmentName,
        Long prescriptionId,
        LocalDateTime appointmentDate,
        String status,
        String diagnosis,
        String treatment,
        String notes,
        LocalDateTime createdAt) { }