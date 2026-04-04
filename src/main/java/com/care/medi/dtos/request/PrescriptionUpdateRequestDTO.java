package com.care.medi.dtos.request;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrescriptionUpdateRequestDTO {
    private String medications;
    private String dosageInstructions;
    private String notes;
}
