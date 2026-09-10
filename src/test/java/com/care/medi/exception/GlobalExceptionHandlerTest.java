package com.care.medi.exception;

import com.care.medi.dtos.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle AccessDeniedException")
    void handleAccessDenied() {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        ResponseEntity<ApiResponse<String>> response = exceptionHandler.handleAccessDenied(ex);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("FORBIDDEN", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle AuthenticationException")
    void handleAuthenticationException() {
        InvalidCredentialsException ex = new InvalidCredentialsException("Invalid creds");
        ResponseEntity<ApiResponse<String>> response = exceptionHandler.handleAuthenticationException(ex);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("UNAUTHORIZED", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException")
    void handleValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("object", "field", "message")));

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleValidationErrors(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle ConstraintViolationException")
    void handleConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        jakarta.validation.Path propertyPath = mock(jakarta.validation.Path.class);
        when(propertyPath.toString()).thenReturn("field");
        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException("Violation", violations);

        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleConstraintViolation(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Collections.singletonMap("field", "must not be null"), response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle MethodArgumentTypeMismatchException")
    void handleTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getRequiredType()).thenReturn((Class) String.class);
        when(ex.getValue()).thenReturn("val");
        when(ex.getName()).thenReturn("param");

        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleTypeMismatch(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("INVALID_PARAMETER_TYPE", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle HttpMessageNotReadableException")
    void handleMalformedJson() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Malformed");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleMalformedJson(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("MALFORMED_REQUEST_BODY", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle MissingServletRequestParameterException")
    void handleMissingParams() {
        MissingServletRequestParameterException ex = new MissingServletRequestParameterException("param", "type");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleMissingParams(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("MISSING_QUERY_PARAMETER", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle HttpRequestMethodNotSupportedException")
    void handleMethodNotSupported() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleMethodNotSupported(ex);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        assertEquals("METHOD_NOT_ALLOWED", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle ResourceNotFoundException")
    void handleNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle DuplicateResourceException")
    void handleDuplicateResource() {
        DuplicateResourceException ex = new DuplicateResourceException("Duplicate");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleDuplicateResource(ex);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("DUPLICATE_RESOURCE", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle InvalidRequestException")
    void handleBadRequest() {
        InvalidRequestException ex = new InvalidRequestException("Bad request");
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleBadRequest(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("BAD_REQUEST", response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle ResourceValidationException")
    void handleCustomValidationException() {
        Map<String, String> errors = Collections.singletonMap("field", "error");
        ResourceValidationException ex = new ResourceValidationException(errors);
        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleCustomValidationException(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(errors, response.getBody().errors());
    }

    @Test
    @DisplayName("Should handle generic Exception")
    void handleGenericException() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        Exception ex = new Exception("Generic error");

        ResponseEntity<ApiResponse<Void>> response = exceptionHandler.handleGenericException(request, ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().errors());
    }
}
