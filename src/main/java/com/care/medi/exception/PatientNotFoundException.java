package com.care.medi.exception;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(String message) {
        super(message);
    }

    public PatientNotFoundException(String message, Exception ex) {
        super(message, ex);
    }
}
