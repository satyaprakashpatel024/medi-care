package com.care.medi.controller;


import com.care.medi.dtos.request.*;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AuthResponse;
import com.care.medi.security.JwtService;
import com.care.medi.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse servletResponse;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @Test
    void testLogin() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("test@example.com");
        request.setPassword("password");
        AuthService.AuthTokens tokens = new AuthService.AuthTokens("access", "refresh", "PATIENT");
        when(authService.login(request)).thenReturn(tokens);
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request, servletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Login successful", response.getBody().message());
        assertEquals("refresh", response.getBody().data().getRefreshToken());
        verify(servletResponse).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testRefresh() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO();
        request.setRefreshToken("oldRefresh");
        AuthService.AuthTokens tokens = new AuthService.AuthTokens("access", "refresh", "PATIENT");
        when(authService.refresh(request)).thenReturn(tokens);
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request, servletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Token refreshed successfully", response.getBody().message());
        assertEquals("refresh", response.getBody().data().getRefreshToken());
        verify(servletResponse).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testLogout() {
        ResponseEntity<ApiResponse<Void>> response = authController.logout(servletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Logout successful", response.getBody().message());
        verify(servletResponse).addHeader(eq("Set-Cookie"), anyString());
    }

    @Test
    void testForgotPassword() {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail("test@example.com");

        ResponseEntity<ApiResponse<Void>> response = authController.forgotPassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("OTP sent to your email successfully", response.getBody().message());
        verify(authService).forgotPassword(request);
    }

    @Test
    void testVerifyOtp() {
        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO();
        request.setEmail("test@example.com");
        request.setOtp("123456");

        ResponseEntity<ApiResponse<Void>> response = authController.verifyOtp(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("OTP verified successfully", response.getBody().message());
        verify(authService).verifyOtp(request);
    }

    @Test
    void testResetPassword() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO();
        request.setEmail("test@example.com");
        request.setOtp("123456");
        request.setNewPassword("newPass");

        ResponseEntity<ApiResponse<Void>> response = authController.resetPassword(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password reset successfully", response.getBody().message());
        verify(authService).resetPassword(request);
    }

    @Test
    void testUpdatePassword() {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");
        request.setConfirmPassword("newPass");
        when(authentication.getName()).thenReturn("test@example.com");

        ResponseEntity<ApiResponse<Void>> response = authController.updatePassword(request, authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Password updated successfully", response.getBody().message());
        verify(authService).updatePassword("test@example.com", request);
    }
}
