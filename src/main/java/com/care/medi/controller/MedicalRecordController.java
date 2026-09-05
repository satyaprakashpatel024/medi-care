package com.care.medi.controller;

import com.care.medi.dtos.request.MedicalRecordRequestDTO;
import com.care.medi.dtos.request.MedicalRecordUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.RecordStatus;
import com.care.medi.services.MedicalRecordService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for creating, retrieving, updating, and deleting patient medical records.
 */
@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * Creates a new medical record, optionally linked to an appointment.
     *
     * @param hospitalId the hospital identifier extracted from the request attribute
     * @param request    the clinical record details payload
     * @return a {@link ResponseEntity} containing the created {@link MedicalRecordResponseDTO} with HTTP status 201
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> createRecord(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @Valid @RequestBody MedicalRecordRequestDTO request) {

        MedicalRecordResponseDTO response =
                medicalRecordService.createRecord(hospitalId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Medical record created successfully", response, HttpStatus.CREATED)
        );
    }

    /**
     * Retrieves a single medical record by its primary key identifier.
     *
     * @param hospitalId the hospital identifier
     * @param id         the unique identifier of the medical record
     * @return a {@link ResponseEntity} wrapping the {@link MedicalRecordResponseDTO}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getRecordById(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long id) {

        MedicalRecordResponseDTO response =
                medicalRecordService.getRecordById(id, hospitalId);

        return ResponseEntity.ok(ApiResponse.success("Medical record fetched successfully", response));
    }

    /**
     * Retrieves the medical record associated with a specific appointment.
     *
     * @param hospitalId    the hospital identifier
     * @param appointmentId the unique identifier of the appointment
     * @return a {@link ResponseEntity} wrapping the {@link MedicalRecordResponseDTO}
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getRecordByAppointment(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long appointmentId) {

        MedicalRecordResponseDTO response =
                medicalRecordService.getRecordByAppointmentId(appointmentId, hospitalId);

        return ResponseEntity.ok(ApiResponse.success("Medical record fetched successfully", response));
    }

    /**
     * Retrieves the complete paginated medical record history for a patient, sorted descending by record date.
     *
     * @param hospitalId the hospital identifier
     * @param patientId  the unique identifier of the patient
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link MedicalRecordListResponseDTO}
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<MedicalRecordListResponseDTO>>> getRecordsByPatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordDate") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<MedicalRecordListResponseDTO> result =
                medicalRecordService.getRecordsByPatient(patientId, hospitalId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Medical records fetched successfully", result));
    }

    /**
     * Retrieves active (non-archived) paginated medical records for a given patient.
     *
     * @param hospitalId the hospital identifier
     * @param patientId  the unique identifier of the patient
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link MedicalRecordListResponseDTO}
     */
    @GetMapping("/patient/{patientId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<MedicalRecordListResponseDTO>>> getActiveRecordsByPatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("recordDate").descending());
        Page<MedicalRecordListResponseDTO> result =
                medicalRecordService.getActiveRecordsByPatient(patientId, hospitalId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Active medical records fetched successfully", result));
    }

    /**
     * Retrieves the most recent active medical record for a patient.
     *
     * @param hospitalId the hospital identifier
     * @param patientId  the unique identifier of the patient
     * @return a {@link ResponseEntity} wrapping the latest {@link MedicalRecordResponseDTO}
     */
    @GetMapping("/patient/{patientId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getLatestRecordByPatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long patientId) {

        MedicalRecordResponseDTO response =
                medicalRecordService.getLatestRecordByPatient(patientId, hospitalId);

        return ResponseEntity.ok(ApiResponse.success("Latest medical record fetched successfully", response));
    }

    /**
     * Retrieves a paginated list of medical records authored by a specific doctor.
     *
     * @param hospitalId the hospital identifier
     * @param doctorId   the unique identifier of the authoring doctor
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link MedicalRecordListResponseDTO}
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<Page<MedicalRecordListResponseDTO>>> getRecordsByDoctor(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordDate") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<MedicalRecordListResponseDTO> result =
                medicalRecordService.getRecordsByDoctor(doctorId, hospitalId, pageable);

        return ResponseEntity.ok(ApiResponse.success("Doctor's medical records fetched successfully", result));
    }

    /**
     * Retrieves a hospital-wide paginated list of medical records filtered by status and date boundaries.
     *
     * @param hospitalId the hospital identifier
     * @param status     the optional status filter
     * @param from       the start date filter (inclusive)
     * @param to         the end date filter (inclusive)
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link MedicalRecordListResponseDTO}
     */
    @GetMapping("/hospital")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<MedicalRecordListResponseDTO>>> getRecordsByHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam(required = false) RecordStatus status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "recordDate") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<MedicalRecordListResponseDTO> result =
                medicalRecordService.getRecordsByHospital(
                        hospitalId, status, from, to, pageable);

        return ResponseEntity.ok(ApiResponse.success("Hospital medical records fetched successfully", result));
    }

    /**
     * Updates clinical details or archives an existing medical record.
     *
     * @param hospitalId the hospital identifier
     * @param id         the unique identifier of the medical record
     * @param request    the update payload
     * @return a {@link ResponseEntity} wrapping the updated {@link MedicalRecordResponseDTO}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> updateRecord(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordUpdateRequestDTO request) {

        MedicalRecordResponseDTO response =
                medicalRecordService.updateRecord(id, hospitalId, request);

        return ResponseEntity.ok(ApiResponse.success("Medical record updated successfully", response));
    }

    /**
     * Permanently deletes an unlinked medical record.
     *
     * @param hospitalId the hospital identifier
     * @param id         the unique identifier of the medical record
     * @return a {@link ResponseEntity} containing a confirmation status message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRecord(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long id) {

        String message = medicalRecordService.deleteRecord(id, hospitalId);

        return ResponseEntity.ok(ApiResponse.success(message, message));
    }
}