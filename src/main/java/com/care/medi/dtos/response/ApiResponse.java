package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object errors,
        int statusCode,
        HttpStatus status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {

    // -------------------------------------------------------------------------
    // Static Factory Methods for Clean Instantiation
    // -------------------------------------------------------------------------

    public static <T> ApiResponse<T> success(String message, T data, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(status.value())
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ApiResponse<Void> error(String message, Object errors, HttpStatus status) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .statusCode(status.value())
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();
    }
}