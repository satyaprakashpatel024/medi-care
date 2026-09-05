package com.care.medi.controller;

import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.AppointmentSummaryResponseDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * REST controller for managing appointments.
 * <p>
 * Exposes endpoints for scheduling, querying, updating, rescheduling,
 * cancelling, and deleting appointment records within a hospital.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentController {

    private final AppointmentServiceImpl appointmentService;

    /**
     * Retrieves appointment details by appointment ID and hospital ID.
     *
     * @param hospitalId the unique identifier of the hospital extracted from the request attribute
     * @param id         the unique identifier of the appointment
     * @return a {@link ResponseEntity} wrapping an {@link ApiResponse} with the {@link AppointmentResponseDTO}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> getAppointmentById(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {
        AppointmentResponseDTO appointmentById = appointmentService.getAppointmentByIdAndHospital(id, hospitalId);
        String msg = String.format("Successfully retrieved appointments for Appointment ID : %d.", id);
        return ResponseEntity.ok(ApiResponse.success(msg, appointmentById));
    }

    /**
     * Retrieves a paginated list of appointment summaries for a hospital on a specific date.
     * Defaults to the current date if not specified.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @param date       the optional filter date (ISO format)
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link AppointmentSummaryResponseDTO}
     */
    @GetMapping("/hospital")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<AppointmentSummaryResponseDTO>>> getAllAppointmentsByHospitalAndDate(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate filterDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);
        Page<AppointmentSummaryResponseDTO> allAppointments = appointmentService.getAllAppointmentsByHospitalAndDate(hospitalId, page, size, sortBy, filterDate);
        String msg = String.format("Successfully retrieved %s appointments for Hospital ID %d on %s.",
                allAppointments.getTotalElements(), hospitalId, filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        return ResponseEntity.ok(ApiResponse.success(msg, allAppointments));
    }

    /**
     * Retrieves paginated appointments filtered by hospital ID, appointment status, and optional date.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param status     the status filter for the appointments
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @param date       the optional filter date (ISO format)
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link AppointmentListResponseDTO}
     */
    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAppointmentByHospitalAndStatusAndDate(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam("status") AppointmentStatus status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate filterDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);

        Page<AppointmentListResponseDTO> appointmentPage = appointmentService
                .getAppointmentsByHospitalAndStatusAndDate(hospitalId, status, page, size, sortBy, filterDate);

        String msg = String.format("Found %d %s appointments for %s.",
                appointmentPage.getNumberOfElements(),
                status.name().toLowerCase(),
                filterDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));

        return ResponseEntity.ok(ApiResponse.success(msg, appointmentPage));
    }

    /**
     * Books a new appointment within a hospital.
     *
     * @param hospitalId the unique identifier of the hospital passed via the request header
     * @param request    the appointment booking details
     * @return a {@link ResponseEntity} containing a 201 Created status, a Location header, and the created {@link AppointmentResponseDTO}
     */
    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> bookAnAppointment(
            @RequestHeader(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestBody @Valid AppointmentRequestDTO request
    ) {
        AppointmentResponseDTO appointment = appointmentService.createAppointment(hospitalId, request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(appointment.appointmentId())
                .toUri();
        return ResponseEntity.created(location)
                .body(ApiResponse.success("Appointment created successfully", appointment, HttpStatus.CREATED));
    }

    /**
     * Reschedules an existing appointment to a new date and time.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param id         the unique identifier of the appointment
     * @param request    the rescheduling details
     * @return a {@link ResponseEntity} containing the updated {@link AppointmentResponseDTO}
     */
    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> rescheduleAppointment(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id,
            @RequestBody @Valid AppointmentRescheduleDTO request
    ) {
        AppointmentResponseDTO response = appointmentService.rescheduleAppointment(id, request, hospitalId);
        String msg = String.format("Successfully rescheduled appointment for Appointment ID : %d.", id);
        return ResponseEntity.accepted().body(
                ApiResponse.success(msg, response, HttpStatus.ACCEPTED)
        );
    }

    /**
     * Updates an existing appointment's details.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param id         the unique identifier of the appointment
     * @param request    the updated appointment information
     * @return a {@link ResponseEntity} containing the modified {@link AppointmentResponseDTO}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> updateAppointment(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id,
            @RequestBody @Valid AppointmentUpdateRequestDTO request
    ) {
        String msg = String.format("Successfully updated appointment for Appointment ID : %d.", id);
        return ResponseEntity.accepted().body(
                ApiResponse.success(msg, appointmentService.updateAppointment(id, hospitalId, request), HttpStatus.ACCEPTED)
        );
    }

    /**
     * Cancels an existing appointment.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param id         the unique identifier of the appointment to cancel
     * @return a {@link ResponseEntity} containing the updated {@link AppointmentResponseDTO}
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> cancelAppointment(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {

        appointmentService.cancelAppointment(id, hospitalId);
        return ResponseEntity.accepted().body(
                ApiResponse.success("Appointment cancelled successfully", appointmentService.getAppointmentByIdAndHospital(id, hospitalId), HttpStatus.ACCEPTED)
        );
    }

    /**
     * Retrieves paginated appointments for a specific patient within a hospital.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param patientId  the unique identifier of the patient
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link AppointmentResponseDTO}
     */
    @GetMapping("/patient/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> getAllAppointmentsByHospitalAndPatientId(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long patientId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<AppointmentResponseDTO> appointmentsByPatient = appointmentService.getAppointmentsByHospitalAndPatient(hospitalId, patientId, page, size, sortBy);
        String msg = String.format("Successfully retrieved appointments for Patient ID : %d.", patientId);
        return ResponseEntity.ok(ApiResponse.success(msg, appointmentsByPatient));
    }

    /**
     * Permanently deletes an appointment by its ID and hospital ID.
     *
     * @param hospitalId the unique identifier of the hospital
     * @param id         the unique identifier of the appointment to delete
     * @return a {@link ResponseEntity} indicating the outcome of the deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponseDTO>> deleteAppointment(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id, hospitalId);
        return ResponseEntity.accepted().body(
                ApiResponse.success("Appointment deleted successfully", null, HttpStatus.ACCEPTED)
        );
    }
}