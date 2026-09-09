package com.care.medi.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NotNull @NonNull HttpServletRequest request,
            @NotNull @NonNull HttpServletResponse response,
            @NotNull @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String jwt = null;
        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName()) || "accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt == null || jwt.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        final String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            String refreshToken = request.getHeader("Refresh-Token");
            if (refreshToken != null && !refreshToken.isBlank()) {
                try {
                    String refreshUserEmail = jwtService.extractUsername(refreshToken);
                    if (refreshUserEmail != null) {
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(refreshUserEmail);
                        if (jwtService.isTokenValid(refreshToken, userDetails)) {
                            java.util.Map<String, Object> extraClaims = new java.util.HashMap<>();
                            Object userId = e.getClaims().get("userId");
                            if (userId != null) extraClaims.put("userId", userId);
                            Object hospitalId = e.getClaims().get("hospitalId");
                            if (hospitalId != null) extraClaims.put("hospitalId", hospitalId);

                            String newAccessToken = jwtService.generateToken(extraClaims, userDetails);
                            
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authToken);

                            org.springframework.http.ResponseCookie cookie = org.springframework.http.ResponseCookie.from("jwt", newAccessToken)
                                    .httpOnly(true)
                                    .path("/")
                                    .maxAge(jwtService.getJwtExpiration() / 1000)
                                    .sameSite("Lax")
                                    .build();
                            response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, cookie.toString());
                            response.addHeader("X-New-Access-Token", newAccessToken);

                            if (hospitalId != null) {
                                request.setAttribute("X-Hospital-Id", ((Number) hospitalId).longValue());
                            }

                            filterChain.doFilter(request, response);
                            return;
                        }
                    }
                } catch (Exception ex) {
                    logger.warn("Automatic refresh failed: {}", ex.getMessage());
                }
            }

            String expiredAt = e.getClaims().getExpiration().toInstant()
                    .atZone(com.care.medi.utils.Constants.ZONE_ID)
                    .format(com.care.medi.utils.Constants.JWT_EXPIRATION_DATE_FORMAT);
            logger.warn("JWT token expired at: {}. Refresh token missing or expired. User logged out.", expiredAt);
            
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Your token is expired and you are logged out of the system. Please log in again.\"}");
            return;
        } catch (Exception e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid JWT token.\"}");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                // Extract hospitalId from token (if present) and set it on the response header for downstream services
                try {
                    Long hospitalId = jwtService.extractHospitalId(jwt);
                    if (hospitalId != null) {
                        request.setAttribute("X-Hospital-Id", hospitalId);
                    }
                } catch (Exception ex) {
                    throw new AuthenticationException("Failed to extract hospitalId from JWT token", ex);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
