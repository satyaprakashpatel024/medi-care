package com.care.medi.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String format) {
    }

    public DuplicateResourceException(String message, Exception ex) {
        super(message, ex);
    }
}
