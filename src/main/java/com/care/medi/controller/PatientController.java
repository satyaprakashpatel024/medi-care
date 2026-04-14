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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientServiceImpl patientService;

    /**
     * Retrieves a paginated list of all patients.
     * <p>
     * This endpoint fetches patient details converted to {@link PatientListResponseDTO}
     * and supports pagination and sorting via request parameters.
     * </p>
     * @param page   the zero-based page index to retrieve (defaults to 0)
     * @param size   the number of records per page (defaults to 5)
     * @param sortBy the property name by which the results should be sorted (defaults to "id")
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} with:
     * <ul>
     * <li>A {@link Page} of {@link PatientListResponseDTO} in the data field</li>
     * <li>A success message</li>
     * <li>HTTP Status 200 (OK)</li>
     * </ul>
     * @see PatientListResponseDTO
     * @see Page
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<PatientListResponseDTO>>> getAllPatients(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<PatientListResponseDTO>>builder()
                        .data(patientService.getAllPatients(page, size, sortBy))
                        .message("Successfully retrieved all Patients details.")
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PatientListResponseDTO>>> getAllPatientsByHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> getPatientByIdAndHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long id
    ) {
        PatientResponseDTO patientById = patientService.getPatientByIdAndHospitalId(hospitalId,id);
        String msg = String.format("Successfully retrieved patient details for id : %d.", id);
        return ResponseEntity.ok(
                ApiResponse.<PatientResponseDTO>builder()
                        .data(patientById)
                        .success(true)
                        .message(msg)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PatientResponseDTO>> savePatientInHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @RequestBody @Valid PatientRequestDTO patientRequestDTO
    ) {
        PatientResponseDTO patient = patientService.createPatientInHospital(hospitalId,patientRequestDTO);
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

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientResponseDTO>> updatePatient(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long id,
            @RequestBody @Valid PatientUpdateRequestDTO patientUpdateRequestDTO
    ) {
        PatientResponseDTO patientResponseDTO = patientService.updatePatientInHospital(id,hospitalId, patientUpdateRequestDTO);
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

    @PostMapping("/{id}/insurance")
    public ResponseEntity<ApiResponse<InsuranceResponseDTO>> assignInsuranceToPatient(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long patientId,
            @RequestBody @Valid InsuranceRequestDTO insuranceRequestDTO) {
        InsuranceResponseDTO insurance = patientService.assignInsurance(patientId, insuranceRequestDTO);
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

    @GetMapping("{id}/insurances")
    public ResponseEntity<ApiResponse<List<InsuranceResponseDTO>>> getAllInsurancesOfPatient(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long id
    ) {
        List<InsuranceResponseDTO> insuranceByPatientId = patientService.getInsuranceByPatientId(id);
        String msg = String.format("Successfully retrieved insurances for id : %d.", id);
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
