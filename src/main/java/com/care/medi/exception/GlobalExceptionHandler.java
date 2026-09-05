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
                ApiResponse.<String>builder()
                        .status(HttpStatus.FORBIDDEN)
                        .message("Access Denied: You do not have permission to access this resource.")
                        .success(false)
                        .errors("FORBIDDEN")
                        .build()
        );
    }

    @ExceptionHandler({InvalidCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<ApiResponse<String>> handleAuthenticationException(Exception ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<String>builder()
                        .status(HttpStatus.UNAUTHORIZED)
                        .message(ex.getMessage())
                        .success(false)
                        .errors("UNAUTHORIZED")
                        .build()
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
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Request validation failed")
                        .success(false)
                        .errors(fieldErrors)
                        .build()
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
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Constraint violation")
                        .success(false)
                        .errors(errors)
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid format";
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), requiredType);

        log.warn("Type mismatch: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(message)
                        .success(false)
                        .errors("INVALID_PARAMETER_TYPE")
                        .build()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMalformedJson(HttpMessageNotReadableException ex) {
        log.warn("Malformed HTTP request body: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message("Malformed JSON body or invalid payload structure")
                        .success(false)
                        .errors("MALFORMED_REQUEST_BODY")
                        .build()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = String.format("Required query parameter '%s' of type %s is missing",
                ex.getParameterName(), ex.getParameterType());
        log.warn("Missing parameter: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(message)
                        .success(false)
                        .errors("MISSING_QUERY_PARAMETER")
                        .build()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.METHOD_NOT_ALLOWED)
                        .message(message)
                        .success(false)
                        .errors("METHOD_NOT_ALLOWED")
                        .build()
        );
    }

    // -------------------------------------------------------------------------
    // Resource & Domain Exceptions
    // -------------------------------------------------------------------------

    @ExceptionHandler({ResourceNotFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFound(Exception ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(ex.getMessage())
                        .success(false)
                        .errors("RESOURCE_NOT_FOUND")
                        .build()
        );
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.CONFLICT)
                        .message(ex.getMessage())
                        .success(false)
                        .errors("DUPLICATE_RESOURCE")
                        .build()
        );
    }

    @ExceptionHandler({BusinessException.class, InvalidRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(RuntimeException ex) {
        log.warn("Business or argument validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(ex.getMessage())
                        .success(false)
                        .errors("BAD_REQUEST")
                        .build()
        );
    }

    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomValidationException(ResourceValidationException ex) {
        log.warn("Resource validation failed: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(ex.getMessage())
                        .success(false)
                        .errors(ex.getErrors())
                        .build()
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
                ApiResponse.<Void>builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("An unexpected error occurred. Please try again later.")
                        .success(false)
                        .errors("INTERNAL_SERVER_ERROR")
                        .build()
        );
    }
}