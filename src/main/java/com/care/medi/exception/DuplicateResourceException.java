package com.care.medi.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String format) {
        super(format);
    }

    public DuplicateResourceException(String message, Exception ex) {
        super(message, ex);
    }
}
