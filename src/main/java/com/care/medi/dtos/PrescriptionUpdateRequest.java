package com.care.medi.dtos;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionUpdateRequest {
    private String medications;
    private String dosageInstructions;
    private String notes;
}
