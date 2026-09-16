package com.care.medi.dtos.request;

import com.care.medi.entity.PaymentStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispenseRequestDTO implements java.io.Serializable {

    @NotNull(message = "Patient ID is required.")
    private Long patientId;

    private Long prescriptionId; // Optional, might be an OTC dispense

    @NotNull(message = "Dispensed By (Staff ID) is required.")
    private Long dispensedById;

    @NotNull(message = "Payment status is required.")
    private PaymentStatus paymentStatus; // e.g. PAID, PENDING

    @NotEmpty(message = "At least one item must be dispensed.")
    private List<DispenseItemRequestDTO> items;
}
