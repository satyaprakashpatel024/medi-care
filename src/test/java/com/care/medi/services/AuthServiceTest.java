package com.care.medi.services;

import com.care.medi.dtos.request.*;
import com.care.medi.entity.OtpTable;
import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.care.medi.exception.InvalidCredentialsException;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.UserNotFoundException;
import com.care.medi.repository.*;
import com.care.medi.security.JwtService;
import com.care.medi.services.kafka.EmailNotificationProducer;
import com.care.medi.utils.Helpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  @SuppressWarnings("unused")
  private DoctorRepository doctorRepository;

  @Mock
  private PatientRepository patientRepository;

  @Mock
  @SuppressWarnings("unused")
  private StaffRepository staffRepository;

  @InjectMocks
  private AuthService authService;

  private Users testUser;

  @BeforeEach
  @SuppressWarnings("unused")
  void setUp() {
    testUser = Users.builder()
      .email("test@example.com")
      .password("encoded_old_password")
      .role(Role.PATIENT)
      .isActive(true)
      .build();
  }

  @Test
  void testLogin_Success() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("password");
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(testUser);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    testUser.setId(1L);
    when(patientRepository.findHospitalIdByUser(1L)).thenReturn(Optional.of(100L));

    when(jwtService.generateToken(anyMap(), eq(testUser))).thenReturn("access_token");
    when(jwtService.generateRefreshToken(anyMap(), eq(testUser))).thenReturn("refresh_token");

    AuthService.AuthTokens tokens = authService.login(request);

    assertEquals("access_token", tokens.accessToken());
    assertEquals("refresh_token", tokens.refreshToken());
    assertEquals("PATIENT", tokens.role());
  }

  @Test
  void testLogin_BadCredentials() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("wrong_password");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenThrow(new BadCredentialsException("Bad credentials"));

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    assertEquals("Invalid email or password", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidCredentialsException when account is disabled")
  void testLogin_DisabledException() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("password");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenThrow(new DisabledException("Account disabled"));

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    assertEquals("Account is disabled. Please contact support.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidCredentialsException when account is locked")
  void testLogin_LockedException() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("password");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenThrow(new LockedException("Account locked"));

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    assertEquals("Account is locked. Please contact support.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidCredentialsException on generic AuthenticationException")
  void testLogin_GenericAuthenticationException() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("test@example.com");
    request.setPassword("password");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenThrow(new AuthenticationServiceException("Generic auth error"));

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    assertEquals("Invalid email or password", exception.getMessage());
  }

  @Test
  @DisplayName("Should resolve hospitalId for DOCTOR role during login")
  void testLogin_DoctorRole() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("doctor@example.com");
    request.setPassword("password");

    Users doctorUser = Users.builder().email("doctor@example.com").password("pass").role(Role.DOCTOR).isActive(true).build();
    doctorUser.setId(2L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(doctorUser);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(doctorRepository.findHospitalIdByUserId(2L)).thenReturn(Optional.of(200L));
    when(jwtService.generateToken(anyMap(), eq(doctorUser))).thenReturn("access_token");
    when(jwtService.generateRefreshToken(anyMap(), eq(doctorUser))).thenReturn("refresh_token");

    AuthService.AuthTokens tokens = authService.login(request);

    assertEquals("DOCTOR", tokens.role());
    verify(doctorRepository).findHospitalIdByUserId(2L);
  }

  @Test
  @DisplayName("Should return empty hospitalId for SUPER_ADMIN role during login")
  void testLogin_SuperAdminRole() {
    LoginRequestDTO request = new LoginRequestDTO();
    request.setEmail("admin@example.com");
    request.setPassword("password");

    Users adminUser = Users.builder().email("admin@example.com").password("pass").role(Role.SUPER_ADMIN).isActive(true).build();
    adminUser.setId(3L);

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(adminUser);
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
    when(jwtService.generateToken(anyMap(), eq(adminUser))).thenReturn("access_token");
    when(jwtService.generateRefreshToken(anyMap(), eq(adminUser))).thenReturn("refresh_token");

    AuthService.AuthTokens tokens = authService.login(request);

    assertEquals("SUPER_ADMIN", tokens.role());
  }

  @Test
  void testRefresh_Success() {
    RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("refresh_token");
    when(jwtService.extractUsername("refresh_token")).thenReturn("test@example.com");
    when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(testUser);
    when(jwtService.isTokenValid("refresh_token", testUser)).thenReturn(true);
    testUser.setId(1L);
    when(patientRepository.findHospitalIdByUser(1L)).thenReturn(Optional.of(100L));

    when(jwtService.generateToken(anyMap(), eq(testUser))).thenReturn("new_access_token");
    when(jwtService.generateRefreshToken(anyMap(), eq(testUser))).thenReturn("new_refresh_token");

    AuthService.AuthTokens tokens = authService.refresh(request);

    assertEquals("new_access_token", tokens.accessToken());
    assertEquals("new_refresh_token", tokens.refreshToken());
    assertEquals("PATIENT", tokens.role());
  }

  @Test
  void testRefresh_InvalidToken() {
    RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("invalid_token");
    when(jwtService.extractUsername("invalid_token")).thenThrow(new RuntimeException("Invalid token"));

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.refresh(request));
    assertEquals("Invalid or expired refresh token", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidCredentialsException when refresh token has null username")
  void testRefresh_NullUsername() {
    RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("token_with_null_user");
    when(jwtService.extractUsername("token_with_null_user")).thenReturn(null);

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.refresh(request));
    assertEquals("Invalid refresh token payload", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidCredentialsException when refresh token is not valid")
  void testRefresh_TokenNotValid() {
    RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("expired_token");
    when(jwtService.extractUsername("expired_token")).thenReturn("test@example.com");
    when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(testUser);
    when(jwtService.isTokenValid("expired_token", testUser)).thenReturn(false);

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.refresh(request));
    assertEquals("Refresh token is expired or invalid", exception.getMessage());
  }

  @Test
  void testForgotPassword_Success() {
    ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO("test@example.com");
    when(usersRepository.existsByEmail("test@example.com")).thenReturn(true);

    authService.forgotPassword(request);

    verify(otpTableRepository).deleteByEmail("test@example.com");
    verify(otpTableRepository).save(any(OtpTable.class));
    verify(emailNotificationProducer).sendOtpNotification(eq(Helpers.getRecipientEmail("test@example.com")), anyString());
  }

  @Test
  void testForgotPassword_UserNotFound() {
    ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO("unknown@example.com");
    when(usersRepository.existsByEmail("unknown@example.com")).thenReturn(false);

    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.forgotPassword(request));
    assertEquals("No account found with email: unknown@example.com", exception.getMessage());
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

    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.verifyOtp(request));
    assertEquals("OTP has expired. Please request a new one.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidRequestException when OTP not found during verify")
  void testVerifyOtp_NotFound() {
    VerifyOtpRequestDTO request = new VerifyOtpRequestDTO("test@example.com", "000000");
    when(otpTableRepository.findByEmailAndOtp("test@example.com", "000000"))
      .thenReturn(Optional.empty());

    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.verifyOtp(request));
    assertEquals("Invalid or expired OTP", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidRequestException when OTP has null expiredAt during verify")
  void testVerifyOtp_NullExpiredAt() {
    VerifyOtpRequestDTO request = new VerifyOtpRequestDTO("test@example.com", "123456");
    OtpTable otpTable = OtpTable.builder()
      .email("test@example.com")
      .otp("123456")
      .expiredAt(null)
      .build();

    when(otpTableRepository.findByEmailAndOtp("test@example.com", "123456"))
      .thenReturn(Optional.of(otpTable));

    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.verifyOtp(request));
    assertEquals("OTP has expired. Please request a new one.", exception.getMessage());
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
    verify(emailNotificationProducer).sendPasswordChangedNotification(eq(Helpers.getRecipientEmail("test@example.com")));
  }

  @Test
  @DisplayName("Should throw InvalidRequestException when OTP not found during reset password")
  void testResetPassword_OtpNotFound() {
    ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "000000", "newPass");
    when(otpTableRepository.findByEmailAndOtp("test@example.com", "000000"))
      .thenReturn(Optional.empty());

    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.resetPassword(request));
    assertEquals("Invalid or expired OTP", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw InvalidRequestException when OTP is expired during reset password")
  void testResetPassword_ExpiredOtp() {
    ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass");
    OtpTable otpTable = OtpTable.builder()
      .email("test@example.com")
      .otp("123456")
      .expiredAt(ZonedDateTime.now().minusMinutes(5))
      .build();

    when(otpTableRepository.findByEmailAndOtp("test@example.com", "123456"))
      .thenReturn(Optional.of(otpTable));

    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.resetPassword(request));
    assertEquals("OTP has expired. Please request a new one.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user not found during reset password")
  void testResetPassword_UserNotFound() {
    ResetPasswordRequestDTO request = new ResetPasswordRequestDTO("test@example.com", "123456", "newPass");
    OtpTable otpTable = OtpTable.builder()
      .email("test@example.com")
      .otp("123456")
      .expiredAt(ZonedDateTime.now().plusMinutes(5))
      .build();

    when(otpTableRepository.findByEmailAndOtp("test@example.com", "123456"))
      .thenReturn(Optional.of(otpTable));
    when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.resetPassword(request));
    assertEquals("No account found with email: test@example.com", exception.getMessage());
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
    verify(emailNotificationProducer).sendPasswordChangedNotification(eq(Helpers.getRecipientEmail("test@example.com")));
  }

  @Test
  void testUpdatePassword_MismatchedConfirmPassword() {
    UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("oldPass123", "newPass123", "differentPass");

    InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.updatePassword("test@example.com", request));
    assertEquals("New password and confirm password do not match", exception.getMessage());
  }

  @Test
  void testUpdatePassword_IncorrectCurrentPassword() {
    UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("wrongOldPass", "newPass123", "newPass123");

    when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("wrongOldPass", "encoded_old_password")).thenReturn(false);

    InvalidCredentialsException exception = assertThrows(InvalidCredentialsException.class, () -> authService.updatePassword("test@example.com", request));
    assertEquals("Current password is incorrect", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw UserNotFoundException when user not found during update password")
  void testUpdatePassword_UserNotFound() {
    UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("oldPass", "newPass", "newPass");

    when(usersRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

    UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> authService.updatePassword("unknown@example.com", request));
    assertEquals("No account found with email: unknown@example.com", exception.getMessage());
  }
}
