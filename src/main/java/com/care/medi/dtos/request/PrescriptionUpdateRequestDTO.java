package com.care.medi.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionUpdateRequestDTO implements java.io.Serializable {

    private String medications;
    private String dosageInstructions;
    private String notes;
}
