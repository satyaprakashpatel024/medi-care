package com.care.medi.dtos.response;

import com.care.medi.entity.Prescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record PrescriptionResponseDTO(
  Long id,
  String patientName,
  Long doctorId,
  String doctorName,
  List<PrescriptionItemResponseDTO> items,
  String notes
) implements java.io.Serializable {

  public static PrescriptionResponseDTO fromEntity(Prescription prescription) {
    return PrescriptionResponseDTO.builder()
      .id(prescription.getId())
      .patientName(String.format("%s %s", prescription.getPatient().getFirstName(), prescription.getPatient().getLastName()))
      .doctorId(prescription.getDoctor().getId())
      .doctorName(String.format("%s %s", prescription.getDoctor().getFirstName(), prescription.getDoctor().getLastName()))
      .items(prescription.getItems() != null ? prescription.getItems().stream().map(PrescriptionItemResponseDTO::toResponse).collect(Collectors.toList()) : null)
      .notes(prescription.getNotes())
      .build();
  }

  public static PrescriptionResponseDTO toResponse(Prescription prescription) {
    return PrescriptionResponseDTO.builder()
      .id(prescription.getId())
      .items(prescription.getItems() != null ? prescription.getItems().stream().map(PrescriptionItemResponseDTO::toResponse).collect(Collectors.toList()) : null)
      .notes(prescription.getNotes())
      .build();
  }
}
