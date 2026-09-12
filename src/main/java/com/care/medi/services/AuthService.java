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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final JwtService jwtService;
    private final StaffRepository staffRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UsersRepository usersRepository;
    private final OtpTableRepository otpTableRepository;
    private final EmailNotificationProducer emailNotificationProducer;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthTokens login(LoginRequestDTO request) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user [{}]: Invalid credentials", Helpers.maskEmail(request.getEmail()));
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (DisabledException e) {
            log.warn("Login failed for user [{}]: Account is disabled", Helpers.maskEmail(request.getEmail()));
            throw new InvalidCredentialsException("Account is disabled. Please contact support.");
        } catch (LockedException e) {
            log.warn("Login failed for user [{}]: Account is locked", Helpers.maskEmail(request.getEmail()));
            throw new InvalidCredentialsException("Account is locked. Please contact support.");
        } catch (AuthenticationException e) {
            log.warn("Authentication failure for user [{}]: {}", Helpers.maskEmail(request.getEmail()), e.getMessage());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Users user = (Users) authenticate.getPrincipal();

        Map<String, Object> extraClaims = new HashMap<>();
        if (user.getId() != null) {
            extraClaims.put("userId", user.getId());
        }

        resolveHospitalId(user).ifPresent(hospitalId -> extraClaims.put("hospitalId", hospitalId));

        log.info("Login successful for user: {}, hospitalId: {}", Helpers.maskEmail(request.getEmail()), extraClaims.get("hospitalId"));
        String accessToken = jwtService.generateToken(extraClaims, user);
        String refreshToken = jwtService.generateRefreshToken(extraClaims, user);

        return new AuthTokens(accessToken, refreshToken, user.getRole().name());
    }

    public AuthTokens refresh(RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        String userEmail;
        try {
            userEmail = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            log.warn(com.care.medi.utils.Constants.LOG_SERVICE_EXCEPTION, "AuthService.refresh", e.getMessage(), e);
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        if (userEmail == null) {
            throw new InvalidCredentialsException("Invalid refresh token payload");
        }

        Users user = (Users) userDetailsService.loadUserByUsername(userEmail);
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidCredentialsException("Refresh token is expired or invalid");
        }

        Map<String, Object> extraClaims = new HashMap<>();
        if (user.getId() != null) {
            extraClaims.put("userId", user.getId());
        }
        resolveHospitalId(user).ifPresent(hospitalId -> extraClaims.put("hospitalId", hospitalId));

        String newAccessToken = jwtService.generateToken(extraClaims, user);
        String newRefreshToken = jwtService.generateRefreshToken(extraClaims, user);

        return new AuthTokens(newAccessToken, newRefreshToken, user.getRole().name());
    }

    private Optional<Long> resolveHospitalId(Users user) {
        if (user.getId() == null || user.getRole() == null) {
            return Optional.empty();
        }

        return switch (user.getRole()) {
            case Role.DOCTOR -> doctorRepository.findHospitalIdByUserId(user.getId());
            case Role.PATIENT -> patientRepository.findHospitalIdByUser(user.getId());
            case Role.STAFF, Role.RECEPTIONIST -> staffRepository.findHospitalIdByUserId(user.getId());
            default -> Optional.empty(); // Global admins or platform owners without a specific hospital assignment
        };
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        String email = request.getEmail();
        boolean exists = usersRepository.existsByEmail(email);
        if(!exists) {
            log.warn("Forgot password request for non-existent email: {}", Helpers.maskEmail(email));
            throw new UserNotFoundException("No account found with email: " + email);
        }
        otpTableRepository.deleteByEmail(email);

        String otp = String.format("%06d", new SecureRandom().nextInt(1000000));

        OtpTable otpEntry = OtpTable.builder()
                .email(email)
                .otp(otp)
                .build();
        otpTableRepository.save(otpEntry);

        emailNotificationProducer.sendOtpNotification(Helpers.getRecipientEmail(email), otp);
        log.info("Sent forgot password OTP via Kafka to: {}", Helpers.maskEmail(email));
    }

    @Transactional(readOnly = true)
    public void verifyOtp(VerifyOtpRequestDTO request) {
        OtpTable otpTable = otpTableRepository.findByEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new InvalidRequestException("Invalid or expired OTP"));

        if (otpTable.getExpiredAt() == null || otpTable.getExpiredAt().isBefore(ZonedDateTime.now())) {
            throw new InvalidRequestException("OTP has expired. Please request a new one.");
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        OtpTable otpTable = otpTableRepository.findByEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new InvalidRequestException("Invalid or expired OTP"));

        if (otpTable.getExpiredAt() == null || otpTable.getExpiredAt().isBefore(ZonedDateTime.now())) {
            throw new InvalidRequestException("OTP has expired. Please request a new one.");
        }

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No account found with email: " + request.getEmail()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        otpTableRepository.deleteByEmail(request.getEmail());
        emailNotificationProducer.sendPasswordChangedNotification(Helpers.getRecipientEmail(request.getEmail()));
        log.info("Password successfully reset for user: {}", Helpers.maskEmail(request.getEmail()));
    }

    @Transactional
    public void updatePassword(String email, UpdatePasswordRequestDTO request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidRequestException("New password and confirm password do not match");
        }

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No account found with email: " + email));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        emailNotificationProducer.sendPasswordChangedNotification(Helpers.getRecipientEmail(email));
        log.info("Password successfully updated for user: {}", Helpers.maskEmail(email));
    }

    public record AuthTokens(String accessToken, String refreshToken, String role) {
    }
}