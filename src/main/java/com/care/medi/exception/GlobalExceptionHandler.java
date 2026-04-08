package com.care.medi.exception;

import com.care.medi.dtos.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleGenericException(Exception ex) {
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors("Internal Server Error...")
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
                .message("Validation failed")
                .success(false)
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message("Validation failed..")
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
    public ResponseEntity<ApiResponse<Map<String, String>>> handleNotFound(ResourceNotFoundException ex) {

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors("Resource not found.")
                .status(HttpStatus.NOT_FOUND)
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBadRequest(IllegalArgumentException ex) {

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors("Bad request.")
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleBusinessException(BusinessException ex) {
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors("Business error.")
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public  ResponseEntity<ApiResponse<Map<String, String>>> handleDuplicateResourceException(DuplicateResourceException ex) {
        ApiResponse<Map<String,String>> apiResponse = ApiResponse.<Map<String, String>>builder()
                .message(ex.getMessage())
                .success(false)
                .errors("Duplicate resource.")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        return  new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(ResourceValidationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Multiple resources were not found..")
                        .errors(ex.getErrors())
                        .build());
    }

    @ExceptionHandler(BadRequestException.class)
    public  ResponseEntity<ApiResponse<Map<String, String>>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.<Map<String, String>>builder()
                                .message(ex.getMessage())
                                .success(false)
                                .errors("Bad request.")
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
    }
}
