package com.care.medi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String message,
        boolean success,
        T data,
        Object errors,
        HttpStatus status
) {
}
