package com.care.medi.utils;

public final class Constants {

    // --- Entity Not Found Messages ---
    public static final String MSG_NOT_FOUND = "%s not found with ID: %s";

    public static final String HOSPITAL_NOT_FOUND = "Hospital not found with ID: ";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found with ID: ";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found with ID: ";
    public static final String PATIENT_NOT_FOUND = "Patient not found with ID: ";
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found with ID: ";
    public static final String INSURANCE_NOT_FOUND = "Insurance details not found with ID: ";
    public static final String USER_NOT_FOUND = "User account not found for ID: ";

    // --- Duplicate/Conflict Messages ---
    public static final String DUPLICATE_EMAIL = "An account with this email already exists: ";
    public static final String DUPLICATE_HOSPITAL = "A hospital with this name is already registered.";
    public static final String DUPLICATE_POLICY = "This insurance policy number is already in use.";
    public static final String DUPLICATE_SPECIALTY = "This specialty is already defined in the system.";
    public static final String DUPLICATE_DOCTOR = "This doctor is already defined in the system.";

    // --- Validation & Business Logic Messages ---
    public static final String INVALID_APPOINTMENT_STATUS = "Action denied: Cannot cancel an appointment that is already ";
    public static final String PRESCRIPTION_REQUIRED = "Medical prescription is mandatory for this action.";
    public static final String VALIDATION_ERROR = "One or more validation errors occurred.";
    public static final String ADDRESS_NOT_FOUND = "Address not found with ID: ";

    private Constants() {
        // Prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}