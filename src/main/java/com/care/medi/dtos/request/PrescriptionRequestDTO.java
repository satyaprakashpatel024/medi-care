package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequestDTO implements java.io.Serializable {

  @NotNull(message = "Patient ID is required.")
  private Long patientId;
  @NotNull(message = "Appointment ID is required.")
  private Long appointmentId;
  @NotNull(message = "Doctor ID is required.")
  private Long doctorId;
  @NotNull(message = "Prescription items are required.")
  private java.util.List<PrescriptionItemRequestDTO> items;
  private String notes;
}
