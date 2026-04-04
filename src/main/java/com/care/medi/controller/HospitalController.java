package com.care.medi.controller;

import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.services.HospitalServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalServiceImpl hospitalService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllHospitals( @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "5") int size,
                                                        @RequestParam(defaultValue = "id") String sortBy) {
        Page<HospitalResponseDTO> allHospitals = hospitalService.getAllHospitals(page,size,sortBy);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK)
                        .message("All hospitals fetched successfully")
                        .data(allHospitals)
                        .success(true)
                        .build()
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getHospitalById(@PathVariable Long id) {
        HospitalResponseDTO hospitalById = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.OK)
                        .message("Hospital fetched successfully")
                        .data(hospitalById)
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createHospital(@Valid @RequestBody HospitalRequestDTO request) {
        HospitalResponseDTO hospital = hospitalService.createHospital(request);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED)
                        .message("Hospital created successfully")
                        .data(hospital)
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public  ResponseEntity<ApiResponse> updateHospital(@PathVariable Long id,@Valid @RequestBody HospitalUpdateRequestDTO request) {
        HospitalResponseDTO hospitalResponseDTO = hospitalService.updateHospital(id, request);
        return ResponseEntity.ok(
                ApiResponse
                        .builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Hospital updated successfully")
                        .data(hospitalResponseDTO)
                        .success(true)
                        .build()
        );
    }
}
