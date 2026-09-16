package com.care.medi.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRequestDTO implements java.io.Serializable {

    @NotBlank(message = "Medication name is required")
    private String name;

    private String manufacturer;

    private String dosageForm;

    @PositiveOrZero(message = "Unit price must be positive or zero")
    private Double unitPrice;

    @PositiveOrZero(message = "Stock quantity must be positive or zero")
    private Integer stockQuantity;

    @PositiveOrZero(message = "Reorder level must be positive or zero")
    private Integer reorderLevel;

    private LocalDate expiryDate;

    private String batchNumber;
}
