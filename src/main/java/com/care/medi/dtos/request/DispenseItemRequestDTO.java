package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispenseItemRequestDTO implements java.io.Serializable {

  @NotNull(message = "Medication ID is required.")
  private Long medicationId;

  @Positive(message = "Quantity must be positive.")
  private Integer quantity;
}
