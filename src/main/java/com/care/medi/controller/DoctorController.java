package com.care.medi.controller;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.*;
import com.care.medi.services.DoctorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/doctors")
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
                        .message("All Doctors fetched successfully..")
                        .data(allDoctors)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> getDoctorById(@PathVariable("id") Long id) {
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

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> createDoctor(@RequestBody DoctorRequestDTO request) {
        DoctorResponseDTO doctor = doctorService.createDoctor(request);
        return ResponseEntity.ok(
                ApiResponse.<DoctorResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .message("Doctor created successfully...")
                        .data(doctor)
                        .success(true)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> updateDoctor(
            @PathVariable("id") Long id, @RequestBody DoctorUpdateRequestDTO request) {
        DoctorResponseDTO doctorResponseDTO = doctorService.updateDoctor(id, request);
        return ResponseEntity.accepted().body(
                ApiResponse.<DoctorResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Doctor updated successfully...")
                        .data(doctorResponseDTO)
                        .success(true)
                        .build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDoctor(@PathVariable("id") Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity
                .accepted()
                .body(ApiResponse
                        .<String>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Doctor deleted successfully...")
                        .success(true)
                        .build());
    }

    @GetMapping("/{id}/appointments")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAllAppointmentsByDoctor(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Page<AppointmentListResponseDTO> appointments = doctorService.getAppointmentsByDoctor(id, page, size, sortBy);

        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message("Doctor's appointments fetched successfully...")
                        .data(appointments)
                        .success(true)
                        .build()
        );
    }
}
