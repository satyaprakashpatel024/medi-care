package com.care.medi.controller;

import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.services.DoctorServiceImpl;
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
    private final DoctorServiceImpl doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<HospitalResponseDTO>>> getAllHospitals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.<Page<HospitalResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message("All hospitals fetched successfully")
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
                ApiResponse.<HospitalResponseDTO>
                                builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Hospital updated successfully")
                        .data(hospitalService.updateHospital(id, request))
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/{hospitalId}/doctors")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getActiveDoctorsByHospital(
            @PathVariable("hospitalId") Long hospitalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(STR."Doctors fetched successfully by HospitalId : \{hospitalId}")
                        .data(doctorService.getActiveDoctorsByHospital(hospitalId, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }


    @GetMapping("/speciality")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getActiveDoctorBySpeciality(
            @RequestParam("speciality") String speciality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(STR."Doctors fetched successfully by Speciality : \{speciality}")
                        .data(doctorService.getActiveDoctorsBySpeciality(speciality, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/{hospitalId}/dept/{departmentId}")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getDoctorsByDepartmentId(
            @PathVariable("hospitalId") Long hospitalId,
            @PathVariable("departmentId") Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(STR."Doctors fetched successfully by DepartmentId : \{departmentId}")
                        .data(doctorService.getActiveDoctorsByDepartmentAndHospital(departmentId, hospitalId, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }
}
