package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.services.PrescriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
public class PrescriptionController {

    private final PrescriptionServiceImpl prescriptionService;

    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<Page<PrescriptionResponseDTO>>> getPrescriptionByPatientId(
            @RequestHeader("X-Hospital-Id") Long hospitalId,
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
}
