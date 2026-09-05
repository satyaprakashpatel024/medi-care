package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;
import com.care.medi.services.HospitalDepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for mapping and unmapping departments to/from hospitals.
 */
@RestController
@RequestMapping("/api/v1/admin/hospitals")
@RequiredArgsConstructor
@Validated
public class AdminHospitalDepartmentController {

    private final HospitalDepartmentService hospitalDepartmentService;

    /**
     * Lists all hospital department mappings.
     */
    @GetMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<HospitalDepartmentResponseDTO>>> getAllHospitalDepartments() {
        return ResponseEntity.ok(
                ApiResponse.success("Hospital departments retrieved successfully", hospitalDepartmentService.findAll())
        );
    }

    /**
     * Maps a department to a hospital.
     */
    @PostMapping("/{hospitalId}/departments/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<HospitalDepartmentResponseDTO>> mapDepartmentToHospital(
            @PathVariable("hospitalId") Long hospitalId,
            @PathVariable("departmentId") Long departmentId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Department mapped to hospital successfully", hospitalDepartmentService.mapDepartmentToHospital(hospitalId, departmentId), HttpStatus.CREATED)
        );
    }

    /**
     * Removes department mapping from a hospital.
     */
    @DeleteMapping("/{hospitalId}/departments/{departmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unmapDepartmentFromHospital(
            @PathVariable("hospitalId") Long hospitalId,
            @PathVariable("departmentId") Long departmentId) {
        hospitalDepartmentService.unmapDepartmentFromHospital(hospitalId, departmentId);
        return ResponseEntity.accepted().body(
                ApiResponse.success("Department unmapped from hospital successfully", null, HttpStatus.ACCEPTED)
        );
    }
}
