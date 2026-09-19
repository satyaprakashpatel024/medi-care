package com.care.medi.dtos.response;

import com.care.medi.entity.DispenseRecord;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record DispenseResponseDTO(
  Long id,
  Long patientId,
  String patientName,
  Long prescriptionId,
  Long dispensedById,
  String dispensedByName,
  Double totalAmount,
  String dispenseStatus,
  String paymentStatus,
  ZonedDateTime dispenseDate,
  List<DispenseItemResponseDTO> items
) implements java.io.Serializable {

  public static DispenseResponseDTO toResponse(DispenseRecord record) {
    return DispenseResponseDTO.builder()
      .id(record.getId())
      .patientId(record.getPatient().getId())
      .patientName(record.getPatient().getFirstName() + " " + record.getPatient().getLastName())
      .prescriptionId(record.getPrescription() != null ? record.getPrescription().getId() : null)
      .dispensedById(record.getDispensedBy().getId())
      .dispensedByName(record.getDispensedBy().getFirstName() + " " + record.getDispensedBy().getLastName())
      .totalAmount(record.getTotalAmount())
      .dispenseStatus(record.getDispenseStatus().name())
      .paymentStatus(record.getPaymentStatus().name())
      .dispenseDate(record.getDispenseDate())
      .items(record.getItems() != null ? record.getItems().stream().map(DispenseItemResponseDTO::toResponse).collect(Collectors.toList()) : null)
      .build();
  }
}
