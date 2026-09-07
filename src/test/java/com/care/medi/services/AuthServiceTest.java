package com.care.medi.services;

import com.care.medi.dtos.request.ForgotPasswordRequestDTO;
import com.care.medi.dtos.request.ResetPasswordRequestDTO;
import com.care.medi.dtos.request.UpdatePasswordRequestDTO;
import com.care.medi.dtos.request.VerifyOtpRequestDTO;
import com.care.medi.entity.OtpTable;
import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.care.medi.exception.InvalidCredentialsException;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.UserNotFoundException;
import com.care.medi.repository.OtpTableRepository;
import com.care.medi.repository.UsersRepository;
import com.care.medi.services.kafka.EmailNotificationProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private OtpTableRepository otpTableRepository;

    @Mock
    private EmailNotificationProducer emailNotificationProducer;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Users testUser;

    @BeforeEach
    void setUp() {
        testUser = Users.builder()
                .email("test@example.com")
                .password("encoded_old_password")
                .role(Role.PATIENT)
                .isActive(true)
                .build();
    }

    @Test
    void testForgotPassword_Success() {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO("test@example.com");
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        authService.forgotPassword(request);

        verify(otpTableRepository).deleteByEmail("test@example.com");
        verify(otpTableRepository).save(any(OtpTable.class));
        verify(emailNotificationProducer).sendOtpNotification(eq("test@example.com"), anyString());
    }

    @Test
    void testForgotPassword_UserNotFound() {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO("unknown@example.com");
        when(usersRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.forgotPassword(request));
    }

    @Test
    void testVerifyOtp_Success() {
        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO("test@example.com", "123456");
        OtpTable otpTable = OtpTable.builder()
                .email("test@example.com")
                .otp("123456")
                .expiredAt(ZonedDateTime.now().plusMinutes(5))
                .build();

        when(otpTableRepository.findByEmailAndOtp("test@example.com", "123456"))
                .thenReturn(Optional.of(otpTable));

        assertDoesNotThrow(() -> authService.verifyOtp(request));
    }

    @Test
    void testVerifyOtp_Expired() {
        VerifyOtpRequestDTO request = new VerifyOtpRequestDTO("test@example.com", "123456");
        OtpTable otpTable = OtpTable.builder()
                .email("test@example.com")
                .otp("123456")
                .expiredAt(ZonedDateTime.now().minusMinutes(1))
                .build();

        when(otpTableRepository.findByEmailAndOtp("test@example.com", "123456"))
                .thenReturn(Optional.of(otpTable));

        assertThrows(InvalidRequestException.class, () -> authService.verifyOtp(request));
    }

    @Test
    void testResetPassword_Success() {
        ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPassword123");
        OtpTable otpTable = OtpTable.builder()
                .email("test@example.com")
                .otp("123456")
                .expiredAt(ZonedDateTime.now().plusMinutes(5))
                .build();

        when(otpTableRepository.findByEmailAndOtp("test@example.com", "123456"))
                .thenReturn(Optional.of(otpTable));
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encoded_new_password");

        authService.resetPassword(request);

        assertEquals("encoded_new_password", testUser.getPassword());
        verify(usersRepository).save(testUser);
        verify(otpTableRepository).deleteByEmail("test@example.com");
    }

    @Test
    void testUpdatePassword_Success() {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("oldPass123", "newPass123", "newPass123");

        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPass123", "encoded_old_password")).thenReturn(true);
        when(passwordEncoder.encode("newPass123")).thenReturn("encoded_new_pass");

        authService.updatePassword("test@example.com", request);

        assertEquals("encoded_new_pass", testUser.getPassword());
        verify(usersRepository).save(testUser);
    }

    @Test
    void testUpdatePassword_MismatchedConfirmPassword() {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("oldPass123", "newPass123", "differentPass");

        assertThrows(InvalidRequestException.class, () -> authService.updatePassword("test@example.com", request));
    }

    @Test
    void testUpdatePassword_IncorrectCurrentPassword() {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("wrongOldPass", "newPass123", "newPass123");

        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongOldPass", "encoded_old_password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.updatePassword("test@example.com", request));
    }
}
