package com.care.medi.exception;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.entity.AppointmentStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleGenericException(Exception ex) {
        ApiResponse<String> response = ApiResponse.<String>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(ex.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // Build a clean ApiResponse
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(ex.getConstraintViolations().stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                ConstraintViolation::getMessage
                        )))
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNotFound(ResourceNotFoundException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(ex.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(IllegalArgumentException ex) {

        ApiResponse<String> response = ApiResponse.<String>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<String>> handleBusinessException(BusinessException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateResourceException(DuplicateResourceException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .message(ex.getMessage())
                .success(false)
                .errors(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(ResourceValidationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errors(ex.getErrors())
                        .build());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<String>builder()
                                .message(ex.getMessage())
                                .success(false)
                                .errors(ex.getMessage())
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<String>builder()
                                .message(ex.getMessage())
                                .success(false)
                                .errors(ex.getMessage())
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String expectedFormat = name.contains("date")
                ? "dd MMMM yyyy, hh:mm am/pm (e.g., 17 April 2026, 10:30 am)"
                : Arrays.toString(AppointmentStatus.values());

        String message = STR."Invalid input for '\{name}'. Expected: \{expectedFormat}";
        return ResponseEntity.badRequest().body(
                ApiResponse.<String>builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .message(message)
                        .success(false)
                        .errors(ex.toString())
                        .build()
        );
    }


    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodValidation(HandlerMethodValidationException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extract property names and messages
        ex.getParameterValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> {
                errors.put(parameterName, error.getDefaultMessage());
            });
        });

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message("Validation failed for one or more parameters")
                .success(false)
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
