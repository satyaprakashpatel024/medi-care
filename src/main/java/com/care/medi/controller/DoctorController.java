package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.services.DoctorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorServiceImpl doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<DoctorListResponseDTO> allDoctors = doctorService.getAllDoctors(page, size, sortBy);
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message("All hospitals fetched successfully")
                        .data(allDoctors)
                        .success(true)
                        .build()
        );
    }
    @GetMapping("/{id}")
    public  ResponseEntity<ApiResponse<DoctorResponseDTO>> getDoctorById(@PathVariable("id") Long id){
        DoctorResponseDTO doctorById = doctorService.getDoctorById(id);
        return ResponseEntity.ok(
                ApiResponse.<DoctorResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .message("Doctor fetched successfully...")
                        .data(doctorById)
                        .success(true)
                        .build()
        );
    }
}
