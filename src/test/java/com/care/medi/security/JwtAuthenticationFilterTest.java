package com.care.medi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    @SuppressWarnings("unused")
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testShouldNotFilter_PublicPaths() throws ServletException {
        // Health check & Documentation endpoints
        when(request.getServletPath()).thenReturn("/api/v1/health");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        when(request.getServletPath()).thenReturn("/swagger-ui/index.html");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        // Auth endpoints
        when(request.getServletPath()).thenReturn("/api/v1/auth/login");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        when(request.getServletPath()).thenReturn("/api/v1/auth/update-password");
        assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));

        // Hospital endpoints
        when(request.getServletPath()).thenReturn("/api/v1/hospitals/1");
        when(request.getMethod()).thenReturn("GET");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        when(request.getMethod()).thenReturn("POST");
        assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));

        // Appointment endpoints
        when(request.getServletPath()).thenReturn("/api/v1/appointments");
        when(request.getMethod()).thenReturn("POST");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));

        when(request.getMethod()).thenReturn("GET");
        assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));
    }

    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void testDoFilterInternal_ValidTokenInHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtService.extractUsername("validToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ValidTokenInCookie() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);
        Cookie[] cookies = {new Cookie("jwt", "validToken")};
        when(request.getCookies()).thenReturn(cookies);
        when(jwtService.extractUsername("validToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_InvalidToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtService.extractUsername("invalidToken")).thenThrow(new JwtException("Invalid token"));

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write(contains("Invalid JWT token."));
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_ExpiredToken_NoRefreshToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");

        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 1000));
        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");
        when(jwtService.extractUsername("expiredToken")).thenThrow(expiredException);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write(contains("Your token is expired"));
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_ExpiredToken_WithValidRefreshToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(request.getHeader("Refresh-Token")).thenReturn("validRefreshToken");

        Claims claims = mock(Claims.class);
        when(claims.get("userId")).thenReturn(123L);
        when(claims.get("hospitalId")).thenReturn(456L);
        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");
        when(jwtService.extractUsername("expiredToken")).thenThrow(expiredException);

        when(jwtService.extractUsername("validRefreshToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validRefreshToken", userDetails)).thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq(userDetails))).thenReturn("newAccessToken");
        when(jwtService.getJwtExpiration()).thenReturn(86400000L);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).addHeader(eq("Set-Cookie"), contains("jwt=newAccessToken"));
        verify(response).addHeader("X-New-Access-Token", "newAccessToken");
        verify(request).setAttribute("X-Hospital-Id", 456L);
        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_ExpiredToken_WithInvalidRefreshToken() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(request.getHeader("Refresh-Token")).thenReturn("invalidRefreshToken");

        Claims claims = mock(Claims.class);
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() - 1000));
        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");
        when(jwtService.extractUsername("expiredToken")).thenThrow(expiredException);

        when(jwtService.extractUsername("invalidRefreshToken")).thenThrow(new JwtException("Invalid refresh token"));

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).write(contains("Your token is expired"));
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_ExpiredToken_InvalidHospitalIdHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(request.getHeader("Refresh-Token")).thenReturn("validRefreshToken");
        when(request.getHeader("X-Hospital-Id")).thenReturn("invalid_number");

        Claims claims = mock(Claims.class);
        when(claims.get("userId")).thenReturn(123L);
        when(claims.get("hospitalId")).thenReturn(null);
        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");
        when(jwtService.extractUsername("expiredToken")).thenThrow(expiredException);

        when(jwtService.extractUsername("validRefreshToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validRefreshToken", userDetails)).thenReturn(true);
        when(jwtService.generateToken(anyMap(), eq(userDetails))).thenReturn("newAccessToken");

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(writer).write(contains("Invalid X-Hospital-Id header."));
        verifyNoInteractions(filterChain);
    }

    @Test
    void testDoFilterInternal_ValidToken_HospitalIdExtractorException() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtService.extractUsername("validToken")).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.isTokenValid("validToken", userDetails)).thenReturn(true);
        when(jwtService.extractHospitalId("validToken")).thenThrow(new JwtException("Failed to extract hospital ID"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

