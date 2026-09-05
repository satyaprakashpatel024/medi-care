package com.care.medi.controller;

import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.services.PatientServiceImpl;
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
import java.util.List;

/**
 * REST controller for managing patient profiles and insurance attachments.
 */
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientServiceImpl patientService;

    /**
     * Retrieves a paginated list of registered patients belonging to a hospital.
     *
     * @param hospitalId the hospital identifier extracted from request attributes
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link PatientListResponseDTO}
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<PatientListResponseDTO>>> getAllPatientsByHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        String msg = String.format("Successfully retrieved patients details for Hospital ID : %d.", hospitalId);
        return ResponseEntity.ok(
                ApiResponse.<Page<PatientListResponseDTO>>builder()
                        .data(patientService.getAllPatientsByHospital(hospitalId, page, size, sortBy))
                        .message(msg)
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    /**
     * Retrieves patient profile information by patient ID and hospital ID.
     *
     * @param hospitalId the hospital identifier
     * @param patientId  the unique identifier of the patient
     * @return a {@link ResponseEntity} wrapping the {@link PatientResponseDTO}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> getPatientByIdAndHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") long hospitalId,
            @PathVariable("id") Long patientId
    ) {
        PatientResponseDTO patientById = patientService.getPatientByIdAndHospitalId(hospitalId, patientId);
        String msg = String.format("Successfully retrieved patient details for id : %d.", patientId);
        return ResponseEntity.ok(
                ApiResponse.<PatientResponseDTO>builder()
                        .data(patientById)
                        .success(true)
                        .message(msg)
                        .build()
        );
    }

    /**
     * Registers a new patient within the specified hospital.
     *
     * @param hospitalId the hospital identifier
     * @param request    the patient creation payload
     * @return a {@link ResponseEntity} with status 201 Created and the created {@link PatientResponseDTO}
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> savePatientInHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestBody @Valid PatientRequestDTO request
    ) {
        PatientResponseDTO patient = patientService.createPatientInHospital(hospitalId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(patient.id())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(
                        ApiResponse
                                .<PatientResponseDTO>builder()
                                .data(patient)
                                .success(true)
                                .message("Patient created successfully.")
                                .build()
                );
    }

    /**
     * Updates an existing patient's details within a hospital.
     *
     * @param hospitalId the hospital identifier
     * @param id         the unique identifier of the patient to update
     * @param request    the updated patient details payload
     * @return a {@link ResponseEntity} wrapping the modified {@link PatientResponseDTO}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> updatePatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id,
            @RequestBody @Valid PatientUpdateRequestDTO request
    ) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatientInHospital(id, hospitalId, request);
        String msg = String.format("Successfully updated patient details for id : %d.", id);
        return ResponseEntity.accepted().body(
                ApiResponse.<PatientResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .data(patientResponseDTO)
                        .success(true)
                        .message(msg)
                        .build()
        );
    }

    /**
     * Attaches an insurance policy to a patient profile.
     *
     * @param hospitalId the hospital identifier
     * @param patientId  the unique identifier of the patient
     * @param request    the insurance information payload
     * @return a {@link ResponseEntity} wrapping the created {@link InsuranceResponseDTO}
     */
    @PostMapping("/{id}/insurance")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<InsuranceResponseDTO>> assignInsuranceToPatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long patientId,
            @RequestBody @Valid InsuranceRequestDTO request) {
        InsuranceResponseDTO insurance = patientService.assignInsurance(patientId, hospitalId, request);
        String msg = String.format("Successfully assigned insurance to patient for id : %d.", patientId);
        return ResponseEntity.accepted().body(
                ApiResponse.<InsuranceResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message(msg)
                        .success(true)
                        .data(insurance)
                        .build()
        );
    }

    /**
     * Retrieves all insurance policies linked to a patient.
     *
     * @param hospitalId the hospital identifier
     * @param patientId  the unique identifier of the patient
     * @return a {@link ResponseEntity} wrapping a list of {@link InsuranceResponseDTO}
     */
    @GetMapping("{id}/insurances")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<List<InsuranceResponseDTO>>> getAllInsurancesOfPatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long patientId
    ) {
        List<InsuranceResponseDTO> insuranceByPatientId = patientService.getInsuranceByPatientId(patientId, hospitalId);
        String msg = String.format("Successfully retrieved insurances for PatientId : %d.", patientId);
        return ResponseEntity.ok(
                ApiResponse.<List<InsuranceResponseDTO>>builder()
                        .data(insuranceByPatientId)
                        .success(true)
                        .message(msg)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}