package com.care.medi.controller;

import com.care.medi.dtos.request.LoginRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AuthResponse;
import com.care.medi.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import com.care.medi.dtos.request.RefreshTokenRequestDTO;
import com.care.medi.security.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication, token issuance, and token management.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * Authenticates a user using their credentials, sets the access JWT in an HttpOnly cookie,
     * and returns the refresh token and user role in the response body.
     */
    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletResponse servletResponse
    ) {
        AuthService.AuthTokens tokens = authService.login(request);

        ResponseCookie cookie = ResponseCookie.from("jwt", tokens.accessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .sameSite("Lax")
                .build();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthResponse authResponse = AuthResponse.builder()
                .refreshToken(tokens.refreshToken())
                .role(tokens.role())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    /**
     * Refreshes the JWT access token using a valid refresh token.
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO request,
            HttpServletResponse servletResponse
    ) {
        AuthService.AuthTokens tokens = authService.refresh(request);

        ResponseCookie cookie = ResponseCookie.from("jwt", tokens.accessToken())
                .httpOnly(true)
                .path("/")
                .maxAge(jwtService.getJwtExpiration() / 1000)
                .sameSite("Lax")
                .build();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        AuthResponse authResponse = AuthResponse.builder()
                .refreshToken(tokens.refreshToken())
                .role(tokens.role())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

    /**
     * Logs out the user by clearing the HttpOnly auth cookie.
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse servletResponse) {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}