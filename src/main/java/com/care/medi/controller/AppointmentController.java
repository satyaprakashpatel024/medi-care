package com.care.medi.controller;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.services.AppointmentServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> getAppointmentById(@PathVariable("id") Long id) {
        AppointmentResponseDTO appointmentById = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .message("Appointment fetched successfully...")
                        .data(appointmentById)
                        .success(true)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAll(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "5") Integer size, @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
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

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> createAppointment(@RequestBody @Valid AppointmentRequestDTO request) {
        AppointmentResponseDTO appointment = appointmentService.createAppointment(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(appointment.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(
                        ApiResponse.<AppointmentResponseDTO>builder()
                                .status(HttpStatus.CREATED)
                                .message("Appointment created successfully...")
                                .data(appointment)
                                .success(true)
                                .build()
                );
    }

    @PatchMapping("/reschedule/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> rescheduleAppointment(@PathVariable("id") Long id, @RequestBody @Valid AppointmentRescheduleDTO request) {
        AppointmentResponseDTO response = appointmentService.rescheduleAppointment(id, request);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Appointment rescheduled successfully...")
                        .data(response)
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> updateAppointment(@PathVariable("id") Long id, @RequestBody @Valid AppointmentUpdateRequestDTO request) {
        AppointmentResponseDTO appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Appointment updated successfully...")
                        .success(true)
                        .data(appointment)
                        .build()
        );
    }

    @PutMapping("{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancelAppointment(@PathVariable("id") Long id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Appointment cancelled successfully...")
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAllByPatientId(
            @PathVariable("id") Long patientId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<AppointmentResponseDTO> appointmentsByPatient = appointmentService.getAppointmentsByPatient(patientId, page, size, sortBy);
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message("Appointments fetched by patient id successfully...")
                        .data(appointmentsByPatient)
                        .success(true)
                        .build()
        );
    }
}
