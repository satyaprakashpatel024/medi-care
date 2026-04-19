package com.care.medi.controller;

import com.care.medi.dtos.request.DepartmentRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.DepartmentResponseDTO;
import com.care.medi.services.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Validated
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DepartmentResponseDTO>>> getAllDepartments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortBy
    ) {
        Page<DepartmentResponseDTO> allDepartments = departmentService.getAllDepartments(page, size, sortBy);
        return ResponseEntity.ok(
                ApiResponse.<Page<DepartmentResponseDTO>>builder()
                        .message("Departments fetched successfully")
                        .data(allDepartments)
                        .status(HttpStatus.OK)
                        .success(true)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> createDepartment(
            @RequestBody @Valid DepartmentRequestDTO departmentRequestDTO) {
        DepartmentResponseDTO department = departmentService.createDepartment(departmentRequestDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(department.id())
                .toUri();
        return ResponseEntity.created(location)
                .body(
                        ApiResponse.<DepartmentResponseDTO>builder()
                                .message("Department created successfully")
                                .data(department)
                                .status(HttpStatus.CREATED)
                                .success(true)
                                .build()
                );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDTO departmentResponse = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(
                ApiResponse.<DepartmentResponseDTO>builder()
                        .success(true)
                        .message("Department found successfully")
                        .data(departmentResponse)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> updateDepartment(
            @PathVariable("id") Long id,
            @RequestBody @Valid DepartmentRequestDTO request
    ) {
        DepartmentResponseDTO response = departmentService.updateDepartment(id, request);
        return ResponseEntity.accepted().body(
                ApiResponse.<DepartmentResponseDTO>builder()
                        .success(true)
                        .message("Department updated successfully")
                        .data(response)
                        .status(HttpStatus.ACCEPTED)
                        .build()
        );
    }
}
