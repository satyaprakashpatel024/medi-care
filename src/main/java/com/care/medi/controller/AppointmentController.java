package com.care.medi.controller;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.services.AppointmentServiceImpl;
import com.care.medi.utils.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> getAppointmentById(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {
        AppointmentResponseDTO appointmentById = appointmentService.getAppointmentByIdAndHospital(id,hospitalId);
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

    @GetMapping("/hospital")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAllAppointmentsByHospitalAndDate(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate filterDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);
        String msg = String.format("Successfully retrieved appointments for Hospital ID %d on %s.",
                hospitalId, filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointmentService.getAllAppointmentsByHospitalAndDate(hospitalId, page, size, sortBy, filterDate))
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAppointmentByHospitalAndStatusAndDate(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam("status") AppointmentStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // 1. Handle Date Logic (Keep the logic but consider moving it to Service later)
        LocalDate filterDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);

        // 2. Fetch Data
        Page<AppointmentListResponseDTO> appointmentPage = appointmentService
                .getAppointmentsByHospitalAndStatusAndDate(hospitalId, status, page, size, sortBy, filterDate);

        // 3. Build Dynamic Message
        String msg = String.format("Found %d %s appointments for %s.",
                appointmentPage.getNumberOfElements(),
                status.name().toLowerCase(),
                filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        return ResponseEntity.ok(
                ApiResponse.<Page<AppointmentListResponseDTO>>builder()
                        .status(HttpStatus.OK)
                        .message(msg)
                        .data(appointmentPage)
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> bookAnAppointment(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestBody @Valid AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO appointment = appointmentService.createAppointment(hospitalId,request);
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
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> rescheduleAppointment(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id,
            @RequestBody @Valid AppointmentRescheduleDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.rescheduleAppointment(id, request,hospitalId);
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
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> updateAppointment(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id,
            @RequestBody @Valid AppointmentUpdateRequestDTO request
    ) {

        String msg = String.format("Successfully updated appointment for Appointment ID : %d.", id);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message(msg)
                        .success(true)
                        .data(appointmentService.updateAppointment(id,hospitalId, request))
                        .build()
        );
    }

    @PutMapping("{id}/cancel")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancelAppointment(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {

        appointmentService.cancelAppointment(id,hospitalId);
        return ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Appointment cancelled successfully")
                        .success(true)
                        .build()
        );
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAllAppointmentsByHospitalAndPatientId(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0")Long hospitalId,
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

    @DeleteMapping("{id}")
    public  ResponseEntity<ApiResponse<AppointmentResponseDTO>> deleteAppointment(
            @RequestHeader(value = "X-Hospital-Id", defaultValue = "0")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {
        appointmentService.deleteAppointmentById(id,hospitalId);
        return  ResponseEntity.accepted().body(
                ApiResponse.<AppointmentResponseDTO>builder()
                        .status(HttpStatus.ACCEPTED)
                        .message("Appointment deleted successfully")
                        .success(true)
                        .build()
        );
    }
}
