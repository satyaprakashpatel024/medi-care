package com.care.medi.controller;

import com.care.medi.dtos.request.DispenseRequestDTO;
import com.care.medi.dtos.request.MedicationRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.DispenseResponseDTO;
import com.care.medi.dtos.response.MedicationResponseDTO;
import com.care.medi.services.PharmacyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pharmacy")
@RequiredArgsConstructor
@Tag(name = "Pharmacy Management", description = "Endpoints for managing medications and dispensing drugs")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    @PostMapping("/medications")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'PHARMACIST')")
    @Operation(summary = "Add a new medication to inventory")
    public ResponseEntity<ApiResponse<MedicationResponseDTO>> addMedication(@Valid @RequestBody MedicationRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medication added successfully", pharmacyService.addMedication(request)));
    }

    @PutMapping("/medications/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'PHARMACIST')")
    @Operation(summary = "Update an existing medication")
    public ResponseEntity<ApiResponse<MedicationResponseDTO>> updateMedication(@PathVariable Long id, @Valid @RequestBody MedicationRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Medication updated successfully", pharmacyService.updateMedication(id, request)));
    }

    @GetMapping("/medications")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'PHARMACIST', 'DOCTOR')")
    @Operation(summary = "Get all medications")
    public ResponseEntity<ApiResponse<Page<MedicationResponseDTO>>> getAllMedications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.ok(ApiResponse.success("Medications retrieved", pharmacyService.getAllMedications(page, size, sortBy)));
    }

    @GetMapping("/medications/low-stock")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'PHARMACIST')")
    @Operation(summary = "Get low stock medications")
    public ResponseEntity<ApiResponse<List<MedicationResponseDTO>>> getLowStockMedications(
            @RequestParam(defaultValue = "50") Integer threshold) {
        return ResponseEntity.ok(ApiResponse.success("Low stock medications retrieved", pharmacyService.getLowStockMedications(threshold)));
    }

    @PostMapping("/dispense")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'PHARMACIST')")
    @Operation(summary = "Dispense medications to a patient")
    public ResponseEntity<ApiResponse<DispenseResponseDTO>> dispenseMedications(@Valid @RequestBody DispenseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Medications dispensed successfully", pharmacyService.dispenseMedications(request)));
    }

    @GetMapping("/dispense/patient/{patientId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'HOSPITAL_ADMIN', 'PHARMACIST', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Get dispense history for a patient")
    public ResponseEntity<ApiResponse<Page<DispenseResponseDTO>>> getPatientDispenseHistory(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dispenseDate") String sortBy) {
        return ResponseEntity.ok(ApiResponse.success("Dispense history retrieved", pharmacyService.getPatientDispenseHistory(patientId, page, size, sortBy)));
    }
}
