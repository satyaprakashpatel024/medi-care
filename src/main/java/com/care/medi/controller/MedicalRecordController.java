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

@RestController
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    // -------------------------------------------------------------------------
    // POST /api/v1/medical-records
    // Create a medical record (optionally linked to an appointment)
    // -------------------------------------------------------------------------

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> createRecord(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @Valid @RequestBody MedicalRecordRequestDTO request) {

        MedicalRecordResponseDTO response =
                medicalRecordService.createRecord(hospitalId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MedicalRecordResponseDTO>builder()
                        .message("Medical record created successfully")
                        .success(true)
                        .data(response)
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/{id}
    // Fetch a single record by ID
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getRecordById(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long id) {

        MedicalRecordResponseDTO response =
                medicalRecordService.getRecordById(id, hospitalId);

        return ResponseEntity.ok(
                ApiResponse.<MedicalRecordResponseDTO>builder()
                        .message("Medical record fetched successfully")
                        .success(true)
                        .data(response)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/appointment/{appointmentId}
    // Fetch the record linked to a specific appointment
    // -------------------------------------------------------------------------

    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getRecordByAppointment(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long appointmentId) {

        MedicalRecordResponseDTO response =
                medicalRecordService.getRecordByAppointmentId(appointmentId, hospitalId);

        return ResponseEntity.ok(
                ApiResponse.<MedicalRecordResponseDTO>builder()
                        .message("Medical record fetched successfully")
                        .success(true)
                        .data(response)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/patient/{patientId}
    // Full paginated history for a patient
    // -------------------------------------------------------------------------

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

        return ResponseEntity.ok(
                ApiResponse.<Page<MedicalRecordListResponseDTO>>builder()
                        .message("Medical records fetched successfully")
                        .success(true)
                        .data(result)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/patient/{patientId}/active
    // Active (non-archived) records for a patient
    // -------------------------------------------------------------------------

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

        return ResponseEntity.ok(
                ApiResponse.<Page<MedicalRecordListResponseDTO>>builder()
                        .message("Active medical records fetched successfully")
                        .success(true)
                        .data(result)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/patient/{patientId}/latest
    // Most recent active record for a patient
    // -------------------------------------------------------------------------

    @GetMapping("/patient/{patientId}/latest")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> getLatestRecordByPatient(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long patientId) {

        MedicalRecordResponseDTO response =
                medicalRecordService.getLatestRecordByPatient(patientId, hospitalId);

        return ResponseEntity.ok(
                ApiResponse.<MedicalRecordResponseDTO>builder()
                        .message("Latest medical record fetched successfully")
                        .success(true)
                        .data(response)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/doctor/{doctorId}
    // Records authored by a specific doctor
    // -------------------------------------------------------------------------

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

        return ResponseEntity.ok(
                ApiResponse.<Page<MedicalRecordListResponseDTO>>builder()
                        .message("Doctor's medical records fetched successfully")
                        .success(true)
                        .data(result)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/medical-records/hospital
    // Hospital-wide filtered list (status, date range)
    // -------------------------------------------------------------------------

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

        return ResponseEntity.ok(
                ApiResponse.<Page<MedicalRecordListResponseDTO>>builder()
                        .message("Hospital medical records fetched successfully")
                        .success(true)
                        .data(result)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/medical-records/{id}
    // Update clinical content or archive a record
    // -------------------------------------------------------------------------

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<MedicalRecordResponseDTO>> updateRecord(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long id,
            @Valid @RequestBody MedicalRecordUpdateRequestDTO request) {

        MedicalRecordResponseDTO response =
                medicalRecordService.updateRecord(id, hospitalId, request);

        return ResponseEntity.ok(
                ApiResponse.<MedicalRecordResponseDTO>builder()
                        .message("Medical record updated successfully")
                        .success(true)
                        .data(response)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/medical-records/{id}
    // Hard delete — only allowed when no appointment is linked
    // -------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRecord(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable Long id) {

        String message = medicalRecordService.deleteRecord(id, hospitalId);

        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message(message)
                        .success(true)
                        .data(message)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}