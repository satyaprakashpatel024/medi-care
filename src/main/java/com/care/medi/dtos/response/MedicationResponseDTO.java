package com.care.medi.dtos.response;

import com.care.medi.entity.Medication;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MedicationResponseDTO(
  Long id,
  String name,
  String manufacturer,
  String dosageForm,
  Double unitPrice,
  Integer stockQuantity,
  Integer reorderLevel,
  LocalDate expiryDate,
  String batchNumber
) implements java.io.Serializable {
  public static MedicationResponseDTO toResponse(Medication medication) {
    return MedicationResponseDTO.builder()
      .id(medication.getId())
      .name(medication.getName())
      .manufacturer(medication.getManufacturer())
      .dosageForm(medication.getDosageForm())
      .unitPrice(medication.getUnitPrice())
      .stockQuantity(medication.getStockQuantity())
      .reorderLevel(medication.getReorderLevel())
      .expiryDate(medication.getExpiryDate())
      .batchNumber(medication.getBatchNumber())
      .build();
  }
}
