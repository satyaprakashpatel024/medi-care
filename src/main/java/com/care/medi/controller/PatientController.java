package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.services.PatientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientServiceImpl patientService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientResponseDTO>>> getAllPatients(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<PatientResponseDTO>>builder()
                        .data(patientService.getAllPatients(page, size, sortBy))
                        .message("Success")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> getPatientById(@PathVariable("id") Long id) {
        PatientResponseDTO patientById = patientService.getPatientById(id);
        return ResponseEntity.ok(
                ApiResponse.<PatientResponseDTO>builder()
                        .data(patientById)
                        .success(true)
                        .message("Patient fetched successfully")
                        .build()
        );
    }

}
