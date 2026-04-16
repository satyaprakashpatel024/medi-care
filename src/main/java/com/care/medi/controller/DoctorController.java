package com.care.medi.controller;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.services.DoctorServiceImpl;
import com.care.medi.utils.Constants;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;


@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorServiceImpl doctorService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getAllActiveDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Page<DoctorListResponseDTO> allDoctors = doctorService.getAllActiveDoctors(page, size, sortBy);
        String msg = "All Doctors fetched successfully ";
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(allDoctors)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> getActiveDoctorByIdAndHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long id) {
        DoctorResponseDTO doctorById = doctorService.getDoctorByIdAndHospital(id,hospitalId);
        String msg = String.format("Doctors Details fetched successfully for Doctor Id : %d and HospitalId : %d",id, hospitalId);
        return ResponseEntity.ok(
                ApiResponse.<DoctorResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(doctorById)
                        .success(true)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getAllActiveDoctorsByHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(String.format("Doctors fetched successfully by HospitalId : %s",hospitalId))
                        .data(doctorService.getAllActiveDoctorsByHospital(hospitalId, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> createDoctorInHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @RequestBody DoctorRequestDTO request
    ) {
        DoctorResponseDTO doctor = doctorService.createDoctorInHospital(hospitalId,request);

        // 1. Create a URI for the new resource (e.g., /doctors/5)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(doctor.id())
                .toUri();

        // 2. Pass the URI into the .created() method
        return ResponseEntity.created(location)
                .body(ApiResponse.<DoctorResponseDTO>builder()
                        .status(HttpStatus.CREATED)
                        .message("Doctor created successfully...")
                        .data(doctor)
                        .success(true)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> updateDoctor(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long id, @RequestBody DoctorUpdateRequestDTO request) {
        DoctorResponseDTO doctorResponseDTO = doctorService.updateDoctorByIdAndHospital(id,hospitalId, request);
        return ResponseEntity.accepted().body(
                ApiResponse.<DoctorResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Doctor updated successfully...")
                        .data(doctorResponseDTO)
                        .success(true)
                        .build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDoctorByIdAndHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long id) {
        doctorService.deleteDoctorByIdAndHospital(id,hospitalId);
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
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAllAppointmentsByDoctorAndHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("id") Long doctorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        LocalDate filterDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);
        String msg = String.format("Successfully retrieved appointments for Doctor ID %d on %s.",
                doctorId, filterDate.format(Constants.HUMAN_DATE_FORMAT));

        Page<AppointmentListResponseDTO> appointments = doctorService.getAppointmentsByDoctorAndHospitalAndDate(doctorId,hospitalId,filterDate, page, size, sortBy);
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointments)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/dept/{departmentId}")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getDoctorsByDepartmentAndHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @PathVariable("departmentId") Long departmentId,
            @RequestParam(defaultValue  = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(String.format("Doctors fetched successfully by DepartmentId : %d ",departmentId))
                        .data(doctorService.getActiveDoctorsByDepartmentAndHospital(departmentId, hospitalId, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/speciality")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getActiveDoctorBySpecialityAndHospital(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
            @RequestParam("speciality") String speciality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        String msg = String.format("Doctors fetched successfully by Speciality : %s",speciality);
        return ResponseEntity.ok(
                ApiResponse.<Page<DoctorListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(doctorService.getActiveDoctorsBySpecialityAndHospital(speciality,hospitalId, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }
}
