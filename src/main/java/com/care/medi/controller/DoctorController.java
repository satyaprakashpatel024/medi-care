package com.care.medi.controller;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.services.DoctorServiceImpl;
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

/**
 * REST controller for managing doctors within hospitals.
 * <p>
 * Handles operations such as registering doctors, fetching doctors across hospitals
 * or departments, updating doctor details, and retrieving associated appointments.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
@Validated
public class DoctorController {

    private final DoctorServiceImpl doctorService;

    /**
     * Retrieves a paginated list of all active doctors across all hospitals.
     *
     * @param page   the page index to retrieve
     * @param size   the number of records per page
     * @param sortBy the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link DoctorListResponseDTO}
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getAllActiveDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Page<DoctorListResponseDTO> allDoctors = doctorService.getAllActiveDoctors(page, size, sortBy);
        String msg = "All Doctors fetched successfully ";
        return ResponseEntity.ok(ApiResponse.success(msg, allDoctors));
    }

    /**
     * Retrieves detailed information of an active doctor by ID within a specific hospital.
     *
     * @param hospitalId the hospital identifier extracted from request attributes
     * @param id         the unique identifier of the doctor
     * @return a {@link ResponseEntity} wrapping the {@link DoctorResponseDTO}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> getActiveDoctorByIdAndHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {
        DoctorResponseDTO doctorById = doctorService.getDoctorByIdAndHospital(id, hospitalId);
        String msg = String.format("Doctors Details fetched successfully for Doctor Id : %d and HospitalId : %d", id, hospitalId);
        return ResponseEntity.ok(ApiResponse.success(msg, doctorById));
    }

    /**
     * Retrieves a paginated list of active doctors associated with a specific hospital.
     *
     * @param hospitalId the hospital identifier
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link DoctorListResponseDTO}
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getAllActiveDoctorsByHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Doctors fetched successfully by HospitalId : %s", hospitalId),
                        doctorService.getAllActiveDoctorsByHospital(hospitalId, page, size, sortBy)
                )
        );
    }

    /**
     * Registers a new doctor under a specific hospital.
     *
     * @param hospitalId the hospital identifier
     * @param request    the doctor registration payload
     * @return a {@link ResponseEntity} with status 201 Created and the created {@link DoctorResponseDTO}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> createDoctorInHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestBody @Valid DoctorRequestDTO request
    ) {
        DoctorResponseDTO doctor = doctorService.createDoctorInHospital(hospitalId, request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(doctor.id())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success("Doctor created successfully...", doctor, HttpStatus.CREATED));
    }

    /**
     * Updates an existing doctor's profile within a hospital.
     *
     * @param hospitalId the hospital identifier
     * @param id         the unique identifier of the doctor
     * @param request    the update details payload
     * @return a {@link ResponseEntity} wrapping the updated {@link DoctorResponseDTO}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponseDTO>> updateDoctor(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id, @RequestBody @Valid DoctorUpdateRequestDTO request) {
        DoctorResponseDTO doctorResponseDTO = doctorService.updateDoctorByIdAndHospital(id, hospitalId, request);
        return ResponseEntity.accepted().body(
                ApiResponse.success("Doctor updated successfully...", doctorResponseDTO, HttpStatus.ACCEPTED)
        );
    }

    /**
     * Deactivates or removes a doctor from a hospital.
     *
     * @param hospitalId the hospital identifier
     * @param id         the unique identifier of the doctor to delete
     * @return a {@link ResponseEntity} containing a confirmation message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteDoctorByIdAndHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long id) {
        doctorService.deleteDoctorByIdAndHospital(id, hospitalId);
        return ResponseEntity
                .accepted()
                .body(ApiResponse.success("Doctor deleted successfully...", null, HttpStatus.ACCEPTED));
    }

    /**
     * Retrieves paginated appointments assigned to a specific doctor for a given date.
     * Defaults to the current date if not provided.
     *
     * @param hospitalId the hospital identifier
     * @param doctorId   the unique identifier of the doctor
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @param date       the optional filter date
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link AppointmentListResponseDTO}
     */
    @GetMapping("/{id}/appointments")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> getAllAppointmentsByDoctorAndHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("id") Long doctorId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate filterDate = (date != null) ? date : LocalDate.now(Constants.ZONE_ID);
        String msg = String.format("Successfully retrieved appointments for Doctor ID %d on %s.",
                doctorId, filterDate.format(Constants.HUMAN_DATE_FORMAT));

        Page<AppointmentListResponseDTO> appointments = doctorService.getAppointmentsByDoctorAndHospitalAndDate(doctorId, hospitalId, filterDate, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success(msg, appointments));
    }

    /**
     * Retrieves a paginated list of active doctors filtered by department and hospital.
     *
     * @param hospitalId   the hospital identifier
     * @param departmentId the unique identifier of the department
     * @param page         the page index to retrieve
     * @param size         the number of records per page
     * @param sortBy       the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link DoctorListResponseDTO}
     */
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getDoctorsByDepartmentAndHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @PathVariable("departmentId") Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        String.format("Doctors fetched successfully by DepartmentId : %d ", departmentId),
                        doctorService.getActiveDoctorsByDepartmentAndHospital(departmentId, hospitalId, page, size, sortBy)
                )
        );
    }

    /**
     * Retrieves a paginated list of active doctors filtered by medical speciality and hospital.
     *
     * @param hospitalId the hospital identifier
     * @param speciality the medical speciality query parameter
     * @param page       the page index to retrieve
     * @param size       the number of records per page
     * @param sortBy     the field name by which to sort results
     * @return a {@link ResponseEntity} wrapping a {@link Page} of {@link DoctorListResponseDTO}
     */
    @GetMapping("/speciality")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'RECEPTIONIST', 'PATIENT')")
    public ResponseEntity<ApiResponse<Page<DoctorListResponseDTO>>> getActiveDoctorBySpecialityAndHospital(
            @RequestAttribute(value = "X-Hospital-Id")
            @Min(value = 1, message = "Hospital ID must be a positive number greater than 0") Long hospitalId,
            @RequestParam("speciality") String speciality,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        String msg = String.format("Doctors fetched successfully by Speciality : %s", speciality);
        return ResponseEntity.ok(
                ApiResponse.success(
                        msg,
                        doctorService.getActiveDoctorsBySpecialityAndHospital(speciality, hospitalId, page, size, sortBy)
                )
        );
    }
}