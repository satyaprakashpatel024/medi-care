package com.care.medi.controller;

import com.care.medi.dtos.request.StaffRequestDTO;
import com.care.medi.dtos.request.StaffUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.StaffResponseDTO;
import com.care.medi.services.StaffService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * REST controller for administrative management of hospital staff members.
 */
@RestController
@RequestMapping("/api/v1/admin/staff")
@RequiredArgsConstructor
@Validated
public class StaffController {

    private final StaffService staffService;

    /**
     * Registers a new staff member under a hospital.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponseDTO>> createStaff(
            @RequestAttribute(value = "X-Hospital-Id", required = false) Long headerHospitalId,
            @RequestBody @Valid StaffRequestDTO request) {
        Long targetHospitalId = headerHospitalId != null ? headerHospitalId : request.getHospitalId().longValue();
        StaffResponseDTO staff = staffService.createStaff(targetHospitalId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(staff.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(ApiResponse.success("Staff member created successfully", staff, HttpStatus.CREATED));
    }

    /**
     * Retrieves a paginated list of all staff members.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<StaffResponseDTO>>> getAllStaff(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.success("Staff list retrieved successfully", staffService.getAllStaff(page, size, sortBy))
        );
    }

    /**
     * Retrieves a paginated list of staff members for a specific hospital.
     */
    @GetMapping("/hospital/{hospitalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Page<StaffResponseDTO>>> getStaffByHospital(
            @PathVariable("hospitalId") @Min(value = 1, message = "Hospital ID must be greater than 0") Long hospitalId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.success("Hospital staff list retrieved successfully", staffService.getStaffByHospital(hospitalId, page, size, sortBy))
        );
    }

    /**
     * Retrieves staff details by staff ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<StaffResponseDTO>> getStaffById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Staff details retrieved successfully", staffService.getStaffById(id))
        );
    }

    /**
     * Updates an existing staff profile.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StaffResponseDTO>> updateStaff(
            @PathVariable("id") Long id,
            @RequestBody @Valid StaffUpdateRequestDTO request) {
        return ResponseEntity.accepted().body(
                ApiResponse.success("Staff profile updated successfully", staffService.updateStaff(id, request), HttpStatus.ACCEPTED)
        );
    }

    /**
     * Removes/deactivates a staff member.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(@PathVariable("id") Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.accepted().body(
                ApiResponse.success("Staff member deleted successfully", null, HttpStatus.ACCEPTED)
        );
    }
}
