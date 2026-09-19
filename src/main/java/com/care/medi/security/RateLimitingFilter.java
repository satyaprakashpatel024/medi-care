package com.care.medi.security;

import com.care.medi.dtos.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Filter for rate-limiting sensitive authentication endpoints to prevent brute-force attacks.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter {

  private final Map<String, UserRequestTracker> requestCounts = new ConcurrentHashMap<>();
  private final ObjectMapper objectMapper;
  @Value("${app.security.rate-limit.enabled:true}")
  private boolean rateLimitEnabled;
  @Value("${app.security.rate-limit.auth-max-requests:10}")
  private int maxRequestsPerWindow;
  @Value("${app.security.rate-limit.window-seconds:60}")
  private int windowSeconds;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    if (!rateLimitEnabled) {
      return true;
    }

    String path = request.getServletPath();
    // Rate limit only sensitive authentication operations
    return !(path.equals("/api/v1/auth/login")
      || path.equals("/api/v1/auth/forgot-password")
      || path.equals("/api/v1/auth/verify-otp")
      || path.equals("/api/v1/auth/reset-password"));
  }

  @Override
  protected void doFilterInternal(
    @NonNull HttpServletRequest request,
    @NonNull HttpServletResponse response,
    @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    String clientIp = getClientIP(request);
    String path = request.getServletPath();
    String trackerKey = clientIp + ":" + path;
    long now = System.currentTimeMillis();
    long windowMillis = windowSeconds * 1000L;

    UserRequestTracker tracker = requestCounts.compute(trackerKey, (key, existing) -> {
      if (existing == null || (now - existing.startTime) > windowMillis) {
        return new UserRequestTracker(now, 1);
      }
      existing.count++;
      return existing;
    });

    if (tracker.count > maxRequestsPerWindow) {
      String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID_MDC_KEY);
      log.warn("Rate limit exceeded for IP: {} on endpoint: {} [Correlation-ID: {}]. Requests: {}",
        clientIp, path, correlationId != null ? correlationId : "N/A", tracker.count);

      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding(StandardCharsets.UTF_8.name());

      ApiResponse<Void> errorResponse = ApiResponse.error(
        "You have made too many requests. Please wait a moment before trying again.",
        "TOO_MANY_REQUESTS",
        HttpStatus.TOO_MANY_REQUESTS
      );

      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
      response.getWriter().flush();
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String getClientIP(HttpServletRequest request) {
    String xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader == null || xfHeader.isBlank()) {
      return request.getRemoteAddr();
    }
    return xfHeader.split(",")[0].trim();
  }

  private static class UserRequestTracker {
    final long startTime;
    int count;

    UserRequestTracker(long startTime, int count) {
      this.startTime = startTime;
      this.count = count;
    }
  }
}
