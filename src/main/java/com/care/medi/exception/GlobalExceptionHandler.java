package com.care.medi.exception;

import com.care.medi.dtos.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------
    // Security Exceptions
    // -------------------------------------------------------------------------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<String>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.error("Access Denied: You do not have permission to access this resource.", "FORBIDDEN", HttpStatus.FORBIDDEN)
        );
    }

    @ExceptionHandler({InvalidCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(Exception ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error(ex.getMessage(), "UNAUTHORIZED", HttpStatus.UNAUTHORIZED)
        );
    }

    // -------------------------------------------------------------------------
    // Client Input & Validation Exceptions
    // -------------------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation failed: {}", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error("Request validation failed", fieldErrors, HttpStatus.BAD_REQUEST)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));

        log.warn("Constraint violation: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error("Constraint violation", errors, HttpStatus.BAD_REQUEST)
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid format";
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), requiredType);

        log.warn("Type mismatch: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(message, "INVALID_PARAMETER_TYPE", HttpStatus.BAD_REQUEST)
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(HttpMessageNotReadableException ex) {
        log.warn("Malformed HTTP request body: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error("Malformed JSON body or invalid payload structure", "MALFORMED_REQUEST_BODY", HttpStatus.BAD_REQUEST)
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = String.format("Required query parameter '%s' of type %s is missing",
                ex.getParameterName(), ex.getParameterType());
        log.warn("Missing parameter: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(message, "MISSING_QUERY_PARAMETER", HttpStatus.BAD_REQUEST)
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                ApiResponse.error(message, "METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED)
        );
    }

    // -------------------------------------------------------------------------
    // Resource & Domain Exceptions
    // -------------------------------------------------------------------------

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(Exception ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND)
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(ex.getMessage(), "DUPLICATE_RESOURCE", HttpStatus.CONFLICT)
        );
    }

    @ExceptionHandler({BusinessException.class, InvalidRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(RuntimeException ex) {
        log.warn("Business or argument validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(ex.getMessage(), "BAD_REQUEST", HttpStatus.BAD_REQUEST)
        );
    }

    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomValidationException(ResourceValidationException ex) {
        log.warn("Resource validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(ex.getMessage(), ex.getErrors(), HttpStatus.BAD_REQUEST)
        );
    }

    // -------------------------------------------------------------------------
    // Catch-All / Fallback
    // -------------------------------------------------------------------------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(HttpServletRequest request, Exception ex) throws Exception {
        if (request.getRequestURI().startsWith("/h2-console")) {
            throw ex; // Safe rethrow without cast
        }

        log.error("Unhandled internal server error occurred at URL: {}", request.getRequestURI(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("An unexpected error occurred. Please try again later.", "INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }
}