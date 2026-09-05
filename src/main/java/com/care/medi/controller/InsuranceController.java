package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.services.InsuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for retrieving insurance policy details.
 */
@RestController
@RequestMapping("/api/v1/insurances")
@RequiredArgsConstructor
public class InsuranceController {

    private final InsuranceService insuranceService;

    /**
     * Retrieves insurance details matching a specific policy number.
     *
     * @param policyNumber the unique policy number identifier
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} wrapping the {@link InsuranceResponseDTO}
     */
    @GetMapping("/{pNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<InsuranceResponseDTO>> getInsurancesByPolicyNumber(
            @PathVariable("pNumber") String policyNumber) {
        InsuranceResponseDTO insuranceByPolicyNumber = insuranceService.getInsuranceByPolicyNumber(policyNumber);
        return ResponseEntity.ok(
                ApiResponse.<InsuranceResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .message("Insurance retrieved successfully for this policy number.")
                        .data(insuranceByPolicyNumber)
                        .success(true)
                        .build()
        );
    }
}