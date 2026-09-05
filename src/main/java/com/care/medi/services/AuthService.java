package com.care.medi.services;

import com.care.medi.dtos.request.LoginRequestDTO;
import com.care.medi.dtos.response.AuthResponse;
import com.care.medi.entity.Role;
import com.care.medi.entity.Users;
import com.care.medi.exception.InvalidCredentialsException;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.PatientRepository;
import com.care.medi.repository.StaffRepository;
import com.care.medi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

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
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequestDTO request) {
        Authentication authenticate;
        try {
            authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            log.info("Login successful for user: {}", request.getEmail());
        } catch (BadCredentialsException e) {
            log.warn("Login failed for user [{}]: Invalid credentials", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (DisabledException e) {
            log.warn("Login failed for user [{}]: Account is disabled", request.getEmail());
            throw new InvalidCredentialsException("Account is disabled. Please contact support.");
        } catch (LockedException e) {
            log.warn("Login failed for user [{}]: Account is locked", request.getEmail());
            throw new InvalidCredentialsException("Account is locked. Please contact support.");
        } catch (AuthenticationException e) {
            log.warn("Authentication failure for user [{}]: {}", request.getEmail(), e.getMessage());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Users user = (Users) authenticate.getPrincipal();

        Map<String, Object> extraClaims = new HashMap<>();
        if (user.getId() != null) {
            extraClaims.put("userId", user.getId());
        }

        resolveHospitalId(user).ifPresent(hospitalId -> extraClaims.put("hospitalId", hospitalId));

        String token = jwtService.generateToken(extraClaims, user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
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
}