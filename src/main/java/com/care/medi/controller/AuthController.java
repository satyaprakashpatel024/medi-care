package com.care.medi.controller;

import com.care.medi.dtos.request.*;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AuthResponse;
import com.care.medi.security.JwtService;
import com.care.medi.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
                .expiresIn(jwtService.getJwtExpiration())
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
                .expiresIn(jwtService.getJwtExpiration())
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

    /**
     * Initiates password reset by sending an OTP to the user's registered email via Kafka.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset OTP via email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email successfully", null));
    }

    /**
     * Verifies account email OTP.
     */
    @PostMapping("/verify-otp")
    @Operation(summary = "Verify account email OTP")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO request) {
        authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success("OTP verified successfully", null));
    }

    /**
     * Resets the user's password using the verified OTP.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    /**
     * Updates the password for an authenticated user.
     */
    @PostMapping("/update-password")
    @Operation(summary = "Update password for logged in user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequestDTO request,
            Authentication authentication
    ) {
        authService.updatePassword(authentication.getName(), request);
        return ResponseEntity.ok(ApiResponse.success("Password updated successfully", null));
    }
}