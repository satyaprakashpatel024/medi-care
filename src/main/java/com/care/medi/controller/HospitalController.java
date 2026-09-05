package com.care.medi.controller;

import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.HospitalListResponseDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.services.HospitalServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing hospital entities.
 * <p>
 * Provides endpoints for retrieving hospital lists, fetching hospital details by ID,
 * creating new hospital records, and updating existing hospital information.
 * </p>
 */
@Validated
@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalServiceImpl hospitalService;

    /**
     * Retrieves a paginated list of all hospitals.
     *
     * @param page   the zero-based page index to retrieve (defaults to 0)
     * @param size   the number of records per page (defaults to 5)
     * @param sortBy the field name by which to sort the results (defaults to "id")
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} wrapping
     * a {@link Page} of {@link HospitalListResponseDTO}
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<HospitalListResponseDTO>>> getAllHospitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        String msg = "Successfully retrieved All Hospital list.";
        return ResponseEntity.ok(ApiResponse.success(msg, hospitalService.getAllHospitals(page, size, sortBy)));
    }

    /**
     * Retrieves detailed information for a specific hospital by its identifier.
     *
     * @param id the unique identifier of the hospital to retrieve
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} wrapping
     *         the {@link HospitalResponseDTO}
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<HospitalResponseDTO>> getHospitalById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success("Hospital fetched successfully", hospitalService.getHospitalById(id)));
    }

    /**
     * Creates a new hospital.
     * <p>
     * Restricted to users with the {@code OWNER} role.
     * </p>
     *
     * @param request the payload containing hospital registration details
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} wrapping
     *         the created {@link HospitalResponseDTO} with HTTP status 201 Created
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @Validated
    public ResponseEntity<ApiResponse<HospitalResponseDTO>> createHospital(@Valid @RequestBody HospitalRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Hospital created successfully", hospitalService.createHospital(request), HttpStatus.CREATED));
    }

    /**
     * Updates an existing hospital's details by its identifier.
     * <p>
     * Restricted to users with the {@code ADMIN} role.
     * </p>
     *
     * @param id      the unique identifier of the hospital to update
     * @param request the payload containing updated hospital details
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} wrapping
     *         the updated {@link HospitalResponseDTO} with HTTP status 202 Accepted
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Validated
    public ResponseEntity<ApiResponse<HospitalResponseDTO>> updateHospital(
            @PathVariable("id") Long id,
            @Valid @RequestBody HospitalUpdateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("Hospital updated successfully", hospitalService.updateHospital(id, request), HttpStatus.ACCEPTED));
    }

}