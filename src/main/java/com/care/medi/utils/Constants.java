package com.care.medi.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Constants {
    public static final String KAFKA_TOPIC_APPOINTMENT_NOTIFICATION = "appointment.emails.notification";
    public static final String KAFKA_TOPIC_OTP_NOTIFICATION = "medicare.otp.notification";
    public static final String KAFKA_TOPIC_PASSWORD_CHANGED_NOTIFICATION = "medicare.password-changed.notification";

    // --- Kafka Consumer Group IDs ---
    public static final String KAFKA_GROUP_APPOINTMENT_NOTIFICATION = "appointment-notification-group";
    public static final String KAFKA_GROUP_OTP_NOTIFICATION = "otp-notification-group";
    public static final String KAFKA_GROUP_PASSWORD_CHANGED_NOTIFICATION = "password-changed-notification-group";
    // --- Entity Not Found Messages ---
    public static final String MSG_NOT_FOUND = "%s not found with ID: %s";

    public static final String HOSPITAL_NOT_FOUND = "Hospital not found with ID: ";
    public static final String DEPARTMENT_NOT_FOUND = "Department not found with ID: ";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found with ID: %s and Hospital ID: %s";
    public static final String PATIENT_NOT_FOUND = "Patient not found with ID: ";
    public static final String PATIENT_NOT_FOUND_IN_HOSPITAL = "Patient not found with ID: %s in this Hospital. Please provide correct patientId or hospitalId";
    public static final String APPOINTMENT_NOT_FOUND = "Appointment not found with ID: ";
    public static final String INSURANCE_NOT_FOUND = "Insurance details not found with ID: ";
    public static final String USER_NOT_FOUND = "User account not found for ID: ";

    // --- Duplicate/Conflict Messages ---
    public static final String DUPLICATE_EMAIL = "An account with this email already exists: ";
    public static final String DUPLICATE_POLICY = "This insurance policy number is already in use.";
    public static final String DUPLICATE_DOCTOR = "This doctor is already defined in the system.";

    // --- Validation & Business Logic Messages ---
    public static final String INVALID_APPOINTMENT_STATUS = "Action denied: Cannot cancel an appointment that is already ";
    public static final String PRESCRIPTION_REQUIRED = "Medical prescription is mandatory for this action.";
    public static final String ADDRESS_NOT_FOUND = "Address not found with ID: ";

    // Set Time Zone
    public static final String TIME_ZONE = "Asia/Kolkata";
    public static final DateTimeFormatter HUMAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    public static final DateTimeFormatter SHORT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter HUMAN_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
    public static final ZoneId ZONE_ID = ZoneId.of(TIME_ZONE); // 10:30 AM
    public static final DateTimeFormatter HUMAN_DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");    // 15 April 2025, 10:30 AM
    public static final DateTimeFormatter JWT_EXPIRATION_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss 'IST'");
    public static final String APPOINTMENT_NOT_FOUND_IN_HOSPITAL = "Appointment not found with ID: %s in this Hospital. Please provide correct appointmentId or hospitalId";
    public static final String INVALID_REQUEST_APPOINTMENT_IS_CANCELLED = "Action denied: Cannot add prescription to an appointment that is already CANCELLED.";
    public static final String CONFLICTING_APPOINTMENT = "Action denied: This doctor is already scheduled for this time.";
    public static final String INVALID_REQUEST_APPOINTMENT_IS_COMPLETED = "Action Denied : Cannot Reschedule Appointment that is already COMPLETED.";

    // notification
    public static final String FAILED_TO_SEND_NOTIFICATION = "Failed to send email to: {}: {}";

    // --- Logging Constants ---
    public static final String LOG_AUTH_FAILURE = "Authentication failed for user [{}]: {}";
    public static final String LOG_TOKEN_EXPIRED = "JWT token expired at: {}. Refresh token missing or expired. User logged out.";
    public static final String LOG_INVALID_TOKEN = "Invalid JWT token: {}";
    public static final String LOG_AUTO_REFRESH_FAILED = "Automatic refresh failed: {}";
    public static final String LOG_INVALID_HEADER = "Invalid {} header: {}";
    public static final String LOG_SERVICE_EXCEPTION = "Exception in service [{}]: {}";
    public static final String LOG_KAFKA_PRODUCE_ERROR = "Failed to send Kafka notification event to topic [{}]: {}";
    public static final String LOG_KAFKA_CONSUME_ERROR = "Error processing Kafka notification event for topic/type [{}]: {}";
    public static final String LOG_EMAIL_SEND_ERROR = "Failed to send email to [{}]: {}";
    public static final String LOG_UNHANDLED_EXCEPTION = "Unhandled internal server error occurred at URL {}: {}";

    // ENCODING
    public static final String ENCODING = "UTF-8";

    private Constants() {
        // Prevent instantiation
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}