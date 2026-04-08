package com.care.medi.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ResourceValidationException extends RuntimeException {
    private final Map<String, String> errors;

    public ResourceValidationException(Map<String, String> errors) {
        super("Validation Failed");
        this.errors = errors;
    }
}
