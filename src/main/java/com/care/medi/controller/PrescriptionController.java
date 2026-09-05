package com.care.medi.controller;

import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.services.PrescriptionServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * REST controller for issuing and retrieving prescriptions.
 */
@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionServiceImpl prescriptionService;

    /**
     * Retrieves a paginated list of prescriptions issued for a specific patient.
     *
     * @param hospitalId the hospital identifier extracted from request attributes
     * @param patientId  the unique identifier of the patient
     * @param page       the zero-based page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link PrescriptionResponseDTO}
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<PrescriptionResponseDTO>>> getPrescriptionByPatientId(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortBy) {
        Page<PrescriptionResponseDTO> prescriptionByPatientId = prescriptionService.getPrescriptionByPatientId(hospitalId, patientId, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success("Prescriptions fetched successfully", prescriptionByPatientId));
    }

    /**
     * Retrieves prescriptions associated with a specific appointment.
     *
     * @param hospitalId    the hospital identifier
     * @param appointmentId the unique identifier of the appointment
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link PrescriptionResponseDTO}
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<PrescriptionResponseDTO>>> getPrescriptionByAppointmentId(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("appointmentId") Long appointmentId
    ) {
        Page<PrescriptionResponseDTO> byId = prescriptionService.getPrescriptionByAppointmentId(hospitalId, appointmentId, 0, 5, "id");

        return ResponseEntity.ok(ApiResponse.success("Prescriptions fetched successfully", byId));
    }

    /**
     * Issues and assigns a prescription to an appointment.
     *
     * @param hospitalId the hospital identifier
     * @param request    the prescription payload
     * @return a {@link ResponseEntity} with status 201 Created and the created {@link PrescriptionResponseDTO}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<PrescriptionResponseDTO>> assignPrescription(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestBody @Valid PrescriptionRequestDTO request
    ) {
        PrescriptionResponseDTO prescription = prescriptionService.assignPrescriptionToAppointment(hospitalId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(prescription.id())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(ApiResponse.success("Prescription created successfully", prescription, HttpStatus.CREATED));
    }
}