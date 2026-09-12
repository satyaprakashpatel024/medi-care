package com.care.medi.utils;

import com.care.medi.entity.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class Helpers {
    // 1. Keep the fields static, but DO NOT put @Value here
    private static final String devEmail = "1008tonystark@gmail.com";
    private static final boolean isDevEnvironment = true;

    private Helpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static LocalDate getStartOfTheDay(LocalDate date) {
        return date.atStartOfDay().toLocalDate();
    }

    public static LocalDate getEndOfTheDay(LocalDate date) {
        return date.atTime(LocalTime.MAX).toLocalDate();
    }


    public static LocalDate parseAppointmentDate(String dateString, Map<String, String> errorMap) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        try {
            // Standard ISO_LOCAL_DATE (yyyy-MM-dd)
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            errorMap.put("appointmentDate", "Invalid date format. Expected: yyyy-MM-dd (e.g., 2026-04-17)");
            return null;
        }
    }

    public static LocalTime parseAppointmentTime(String appointmentTime, Map<String, String> errorMap) {

        if (appointmentTime == null || appointmentTime.trim().isEmpty()) {
            errorMap.put("appointmentTime", "Time is required");
            return null;
        }
        try {
            String time = appointmentTime.trim().toUpperCase(Locale.ENGLISH);
            return LocalTime.parse(time, Constants.HUMAN_TIME_FORMAT);
        } catch (Exception e) {
            errorMap.put("appointmentTime", "Invalid format. Expected: 10:00 AM");
            return null;
        }
    }

    public static String getRecipientEmail(String email) {
        if (Helpers.isDevEnvironment) {
            // Dev/Test environment: Route everything to the developer group
            return devEmail;
        } else {
            // Production environment: Send to the actual user email
            return email;
        }
    }

    public static String getRecipientEmail(Patient patientEntity) {
        if (patientEntity == null || patientEntity.getUser() == null) {
            return Helpers.isDevEnvironment ? devEmail : null;
        }
        return getRecipientEmail(patientEntity.getUser().getEmail());
    }

    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return "***";
        }
        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (local.length() <= 2) {
            return local.charAt(0) + "***" + domain;
        }
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + domain;
    }

    public static String maskName(String name) {
        if (name == null || name.isBlank()) {
            return "***";
        }
        String[] parts = name.trim().split("\\s+");
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                masked.append(part.charAt(0)).append("***");
                if (i < parts.length - 1) {
                    masked.append(" ");
                }
            }
        }
        return masked.toString();
    }

    public static String maskOtp(String otp) {
        if (otp == null || otp.isBlank()) {
            return "******";
        }
        return "******";
    }

    public static String maskKey(String key) {
        if (key == null) {
            return "***";
        }
        if (key.contains("@")) {
            return maskEmail(key);
        }
        return key;
    }
}
