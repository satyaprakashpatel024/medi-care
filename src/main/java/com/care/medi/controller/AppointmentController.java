package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.services.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAll(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer size, @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message("All appointments fetched successfully...")
                        .data(appointmentService.getAllAppointments(page, size, sortBy))
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAppointmentByStatus(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message("Appointments fetched by Status successfully...")
                        .data(appointmentService.getAppointmentsByStatus(status, page, size, sortBy))
                        .success(true)
                        .build()
        );
    }
}
