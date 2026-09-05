package com.care.medi.controller;

import com.care.medi.dtos.request.UserRoleUpdateRequestDTO;
import com.care.medi.dtos.request.UserStatusUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.UserResponseDTO;
import com.care.medi.services.UserAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for system user governance and role/status administration.
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {

    private final UserAdminService userAdminService;

    /**
     * Retrieves a paginated list of all system users.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", userAdminService.getAllUsers(page, size, sortBy))
        );
    }

    /**
     * Retrieves user details by user ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("User fetched successfully", userAdminService.getUserById(id))
        );
    }

    /**
     * Assigns/updates a user security role.
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserRole(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserRoleUpdateRequestDTO request) {
        return ResponseEntity.accepted().body(
                ApiResponse.success("User role updated successfully", userAdminService.updateUserRole(id, request.getRole()), HttpStatus.ACCEPTED)
        );
    }

    /**
     * Enables or disables a user account.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUserStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid UserStatusUpdateRequestDTO request) {
        return ResponseEntity.accepted().body(
                ApiResponse.success("User status updated successfully", userAdminService.updateUserStatus(id, request.getIsActive()), HttpStatus.ACCEPTED)
        );
    }
}
