package com.care.medi.controller;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.services.AppointmentServiceImpl;
import com.care.medi.utils.Constants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> getAppointmentById(@PathVariable("id") Long id) {
        AppointmentResponseDTO appointmentById = appointmentService.getAppointmentById(id);
        String msg = String.format("Successfully retrieved appointments for Appointment ID : %d.", id);
        return ResponseEntity.ok(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointmentById)
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/hospital/{id}")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAllAppointmentsByHospitalAndDate(
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate filterDate = (date != null) ? date : LocalDate.now(ZoneId.of(Constants.TIME_ZONE));
        String msg = String.format("Successfully retrieved appointments for Hospital ID %d on %s.",
                id, filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointmentService.getAllAppointmentsByHospitalAndDate(id, page, size, sortBy, filterDate))
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/hospital/{id}/status")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAppointmentByHospitalAndStatusAndDate(
            @PathVariable("id") Long id,
            @RequestParam String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate filterDate = (date != null) ? date : LocalDate.now(ZoneId.of(Constants.TIME_ZONE));
        String msg = String.format("Successfully retrieved %s appointments for %s.",
                status,
                filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointmentService.getAppointmentsByHospitalAndStatusAndDate(id, status, page, size, sortBy, filterDate))
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> bookAnAppointment(@RequestBody @Valid AppointmentRequestDTO request) {
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
                                .message("Appointment created successfully")
                                .data(appointment)
                                .success(true)
                                .build()
                );
    }

    @PatchMapping("/reschedule/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> rescheduleAppointment(@PathVariable("id") Long id, @RequestBody @Valid AppointmentRescheduleDTO request) {
        AppointmentResponseDTO response = appointmentService.rescheduleAppointment(id, request);
        String msg = String.format("Successfully rescheduled appointment for Appointment ID : %d.", id);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message(msg)
                        .data(response)
                        .success(true)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> updateAppointment(@PathVariable("id") Long id, @RequestBody @Valid AppointmentUpdateRequestDTO request) {
        AppointmentResponseDTO appointment = appointmentService.updateAppointment(id, request);
        String msg = String.format("Successfully updated appointment for Appointment ID : %d.", id);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message(msg)
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
                        .message("Appointment cancelled successfully")
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/hospital/{hospitalId}/patient/{id}")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAllAppointmentsByHospitalAndPatientId(
            @PathVariable("hospitalId") Long hospitalId,
            @PathVariable("id") Long patientId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<AppointmentResponseDTO> appointmentsByPatient = appointmentService.getAppointmentsByHospitalAndPatient(hospitalId, patientId, page, size, sortBy);
        String msg = String.format("Successfully retrieved appointments for Patient ID : %d.", patientId);
        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointmentsByPatient)
                        .success(true)
                        .build()
        );
    }
}
