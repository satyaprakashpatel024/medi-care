package com.care.medi.dtos.response;

import com.care.medi.entity.Prescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PrescriptionResponseDTO(
        Long id,
        String patientName,
        Long doctorId,
        String doctorName,
        String medications,
        String dosageInstructions,
        String notes
) {
    public  static PrescriptionResponseDTO fromEntity(Prescription prescription) {
        return PrescriptionResponseDTO.builder()
                .id(prescription.getId())
                .notes(prescription.getNotes())
                .doctorId(prescription.getDoctor().getId())
                .doctorName(STR."\{prescription.getDoctor().getFirstName()} \{prescription.getDoctor().getLastName()}")
                .medications(prescription.getMedications())
                .dosageInstructions(prescription.getDosageInstructions())
                .build();
    }
}
