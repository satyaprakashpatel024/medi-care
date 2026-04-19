package com.care.medi.controller;

import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.HospitalListResponseDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.services.HospitalServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hospitals")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalServiceImpl hospitalService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HospitalListResponseDTO>>> getAllHospitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        String msg = "Successfully retrieved All Hospital list.";
        return ResponseEntity.ok(
                ApiResponse.<Page<HospitalListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(hospitalService.getAllHospitals(page, size, sortBy))
                        .success(true)
                        .build()
        );

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalResponseDTO>> getHospitalById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                ApiResponse.<HospitalResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .message("Hospital fetched successfully")
                        .data(hospitalService.getHospitalById(id))
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HospitalResponseDTO>> createHospital(@Valid @RequestBody HospitalRequestDTO request) {
        return ResponseEntity.ok(
                ApiResponse.<HospitalResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .message("Hospital created successfully")
                        .data(hospitalService.createHospital(request))
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<HospitalResponseDTO>> updateHospital(@PathVariable("id") Long id, @Valid @RequestBody HospitalUpdateRequestDTO request) {
        return ResponseEntity.ok(
                ApiResponse.<HospitalResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Hospital updated successfully")
                        .data(hospitalService.updateHospital(id, request))
                        .success(true)
                        .build()
        );
    }

}
