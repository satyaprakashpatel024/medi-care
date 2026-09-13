package com.care.medi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        rateLimitingFilter = new RateLimitingFilter(objectMapper);

        ReflectionTestUtils.setField(rateLimitingFilter, "rateLimitEnabled", true);
        ReflectionTestUtils.setField(rateLimitingFilter, "maxRequestsPerWindow", 3);
        ReflectionTestUtils.setField(rateLimitingFilter, "windowSeconds", 60);
    }

    @Test
    @DisplayName("Requests under threshold should pass through filter")
    void testRequestsUnderLimitPass() throws Exception {
        when(request.getServletPath()).thenReturn("/api/v1/auth/login");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        for (int i = 0; i < 3; i++) {
            rateLimitingFilter.doFilterInternal(request, response, filterChain);
        }

        verify(filterChain, times(3)).doFilter(request, response);
    }

    @Test
    @DisplayName("Requests exceeding limit should return HTTP 429 and JSON error response")
    void testRequestExceedingLimitReturns429() throws Exception {
        when(request.getServletPath()).thenReturn("/api/v1/auth/login");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);

        for (int i = 0; i < 4; i++) {
            rateLimitingFilter.doFilterInternal(request, response, filterChain);
        }

        verify(filterChain, times(3)).doFilter(request, response);
        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

        String jsonResponse = stringWriter.toString();
        assertTrue(jsonResponse.contains("TOO_MANY_REQUESTS"));
        assertTrue(jsonResponse.contains("You have made too many requests. Please wait a moment before trying again."));
    }
}
