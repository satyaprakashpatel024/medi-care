package com.care.medi.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class Helpers {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
            return LocalTime.parse(appointmentTime.toUpperCase(), formatter);

        } catch (Exception e) {
            errorMap.put("appointmentTime", "Invalid format. Expected: 10:30 AM");
            return null;
        }
    }
}
