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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionServiceImpl prescriptionService;

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<Page<PrescriptionResponseDTO>>> getPrescriptionByPatientId(
            @RequestHeader(value = "X-Hospital-Id",defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("patientId") Long patientId,
            @RequestParam(value = "page",defaultValue = "0") int page,
            @RequestParam(value = "size",defaultValue = "5") int size,
            @RequestParam(value = "sort",defaultValue = "id") String sortBy) {
        Page<PrescriptionResponseDTO> prescriptionByPatientId = prescriptionService.getPrescriptionByPatientId(hospitalId,patientId,page,size,sortBy);
        return ResponseEntity.ok(
                ApiResponse.<Page<PrescriptionResponseDTO>>builder()
                        .data(prescriptionByPatientId)
                        .message("Prescriptions fetched successfully")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @GetMapping({"/appt/{appointmentId}"})
    public  ResponseEntity<ApiResponse<Page<PrescriptionResponseDTO>>> getPrescriptionByAppointmentId(
            @RequestHeader(value = "X-Hospital-Id",defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("appointmentId") Long appointmentId
    ){
        Page<PrescriptionResponseDTO> byId = prescriptionService.getPrescriptionByAppointmentId(hospitalId, appointmentId, 0, 5, "id");

        return ResponseEntity.ok(
                ApiResponse.<Page<PrescriptionResponseDTO>>builder()
                        .data(byId)
                        .message("Prescriptions fetched successfully")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PrescriptionResponseDTO>> assignPrescription(
            @RequestHeader(value = "X-Hospital-Id",defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestBody @Valid PrescriptionRequestDTO request
    ){
        PrescriptionResponseDTO prescription = prescriptionService.assignPrescriptionToAppointment(hospitalId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(hospitalId)
                .toUri();
        return ResponseEntity
                .created(location)
                .body(
                ApiResponse.<PrescriptionResponseDTO>builder()
                        .data(prescription)
                        .message("Prescription created successfully")
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .build()
        );
    }


}
