package com.care.medi.services;

import com.care.medi.dtos.request.LoginRequestDTO;
import com.care.medi.dtos.response.AuthResponse;
import com.care.medi.entity.Users;
import com.care.medi.exception.InvalidCredentialsException;
import com.care.medi.repository.UsersRepository;
import com.care.medi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final com.care.medi.repository.StaffRepository staffRepository;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password", e);
        }
        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Build extra claims: always include userId; include hospitalId when the user is associated with a staff record
        java.util.Map<String, Object> extra = new java.util.HashMap<>();
        if (user.getId() != null) extra.put("userId", user.getId());
        try {
            staffRepository.findByUserId(user.getId()).ifPresent(staff -> {
                if (staff.getHospital() != null && staff.getHospital().getId() != null) {
                    extra.put("hospitalId", staff.getHospital().getId());
                }
            });
        } catch (Exception ignored) {
            // if repository not available or any issue, skip hospital claim
        }

        String token = jwtService.generateToken(extra, user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
