package com.care.medi.controller;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.services.DoctorServiceImpl;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorServiceImpl doctorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getAllActiveDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Page<DoctorListResponseDTO> allDoctors = doctorService.getAllActiveDoctors(page, size, sortBy);
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
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        LocalDate filterDate = (date != null) ? date : LocalDate.now(ZoneId.of(Constants.TIME_ZONE));
        String msg = String.format("Successfully retrieved appointments for Doctor ID %d on %s.",
                id, filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        Page<AppointmentListResponseDTO> appointments = doctorService.getAppointmentsByDoctorAndDate(id, page, size, sortBy, filterDate);
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointments)
                        .success(true)
                        .build()
        );
    }
}
