package com.care.medi.controller;

import com.care.medi.dtos.request.LoginRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AuthResponse;
import com.care.medi.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication and token issuance.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user using their credentials and issues an authentication token.
     *
     * @param request the login credentials payload
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} wrapping the {@link AuthResponse}
     */
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message("Login successful")
                        .data(response)
                        .build()
        );
    }
}