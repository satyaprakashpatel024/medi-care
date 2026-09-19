package com.care.medi.dtos.response;

import com.care.medi.entity.DispenseItem;
import lombok.Builder;

@Builder
public record DispenseItemResponseDTO(
  Long id,
  Long medicationId,
  String medicationName,
  Integer quantity,
  Double unitPrice,
  Double subTotal
) implements java.io.Serializable {

  public static DispenseItemResponseDTO toResponse(DispenseItem item) {
    return DispenseItemResponseDTO.builder()
      .id(item.getId())
      .medicationId(item.getMedication().getId())
      .medicationName(item.getMedication().getName())
      .quantity(item.getQuantity())
      .unitPrice(item.getUnitPrice())
      .subTotal(item.getSubTotal())
      .build();
  }
}
