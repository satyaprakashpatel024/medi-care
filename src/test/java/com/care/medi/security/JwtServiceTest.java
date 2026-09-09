package com.care.medi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    @InjectMocks
    private JwtService jwtService;
    @Mock
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1000 * 60 * 60 * 24); // 1 day
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 1000 * 60 * 60 * 24 * 7); // 7 days
    }

    @Test
    void testGenerateTokenAndExtractUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        String username = jwtService.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void testExtractUserId() {
        when(userDetails.getUsername()).thenReturn("testuser");
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 123L);

        String token = jwtService.generateToken(claims, userDetails);
        Long userId = jwtService.extractUserId(token);

        assertEquals(123L, userId);
    }

    @Test
    void testExtractHospitalId() {
        when(userDetails.getUsername()).thenReturn("testuser");
        Map<String, Object> claims = new HashMap<>();
        claims.put("hospitalId", 456L);

        String token = jwtService.generateToken(claims, userDetails);
        Long hospitalId = jwtService.extractHospitalId(token);

        assertEquals(456L, hospitalId);
    }

    @Test
    void testIsTokenValid() {
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenInvalidDueToWrongUsername() {
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);

        UserDetails wrongUser = mock(UserDetails.class);
        when(wrongUser.getUsername()).thenReturn("wronguser");

        assertFalse(jwtService.isTokenValid(token, wrongUser));
    }
}
