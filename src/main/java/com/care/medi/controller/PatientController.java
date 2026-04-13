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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientServiceImpl patientService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientListResponseDTO>>> getAllPatients(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<PatientListResponseDTO>>builder()
                        .data(patientService.getAllPatients(page, size, sortBy))
                        .message("Success")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/hospital/{id}")
    public ResponseEntity<ApiResponse<Page<PatientListResponseDTO>>> getAllPatientsByHospital(
            @PathVariable("id") Long hospitalId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<PatientListResponseDTO>>builder()
                        .data(patientService.getAllPatientsByHospital(hospitalId, page, size, sortBy))
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

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponseDTO>> savePatient(@RequestBody @Valid PatientRequestDTO patientRequestDTO) {
        PatientResponseDTO patient = patientService.createPatient(patientRequestDTO);
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
                                .message("Patient created successfully")
                                .build()
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> updatePatient(@PathVariable("id") Long id, @RequestBody @Valid PatientUpdateRequestDTO patientUpdateRequestDTO) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatient(id, patientUpdateRequestDTO);
        return ResponseEntity.accepted().body(
                ApiResponse.<PatientResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .data(patientResponseDTO)
                        .success(true)
                        .message("Patient updated successfully")
                        .build()
        );
    }

    @PostMapping("/{id}/insurance")
    public ResponseEntity<ApiResponse<InsuranceResponseDTO>> assignInsuranceToPatient(
            @PathVariable("id") Long id,
            @RequestBody @Valid InsuranceRequestDTO insuranceRequestDTO) {
        InsuranceResponseDTO insurance = patientService.assignInsurance(id, insuranceRequestDTO);
        return ResponseEntity.accepted().body(
                ApiResponse.<InsuranceResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Insurance assigned successfully")
                        .success(true)
                        .data(insurance)
                        .build()
        );
    }

    @GetMapping("{id}/insurances")
    public ResponseEntity<ApiResponse<List<InsuranceResponseDTO>>> getAllInsurances(@PathVariable("id") Long id) {
        List<InsuranceResponseDTO> insuranceByPatientId = patientService.getInsuranceByPatientId(id);
        return ResponseEntity.ok(
                ApiResponse.<List<InsuranceResponseDTO>>builder()
                        .data(insuranceByPatientId)
                        .success(true)
                        .message("Insurances found for this provided patient id.")
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
