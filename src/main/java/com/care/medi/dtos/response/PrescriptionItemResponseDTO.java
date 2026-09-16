package com.care.medi.dtos.response;

import com.care.medi.entity.PrescriptionItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PrescriptionItemResponseDTO(
        Long id,
        Long medicationId,
        String medicationName,
        String dosageInstructions,
        String notes
) implements java.io.Serializable {

    public static PrescriptionItemResponseDTO toResponse(PrescriptionItem item) {
        return PrescriptionItemResponseDTO.builder()
                .id(item.getId())
                .medicationId(item.getMedication().getId())
                .medicationName(item.getMedication().getName())
                .dosageInstructions(item.getDosageInstructions())
                .notes(item.getNotes())
                .build();
    }
}
