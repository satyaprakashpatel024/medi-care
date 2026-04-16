package com.care.medi.utils;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import java.util.Locale;
import java.util.Map;

public class Helpers {
    public static ZonedDateTime getStartOfTheDay(LocalDate date) {
        return date.atStartOfDay(Constants.ZONE_ID);
    }

    public static ZonedDateTime getEndOfTheDay(LocalDate date) {
        return date.atTime(LocalTime.MAX).atZone(Constants.ZONE_ID);
    }

    public static ZonedDateTime parseAndRoundToNearestTenMinutes(String dateString, Map<String, String> errorMap) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }
        // 1. Parse and Normalize Date
        try {
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("dd MMMM yyyy, hh:mm a")
                    .toFormatter(Locale.ENGLISH);

            // 2. Parse string to LocalDateTime (it doesn't have a zone yet)
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
            // Parse using your helper
            ZonedDateTime rawTime =  localDateTime.atZone(Constants.ZONE_ID);
            // Apply the 10-minute rounding
            ZonedDateTime normalizedTime = Helpers.roundToNearestTenMinutes(rawTime);
            // Validation: Must be in the future
            ZonedDateTime now = ZonedDateTime.now(Constants.ZONE_ID);
            if (normalizedTime != null && normalizedTime.isBefore(now)) {
                errorMap.put("appointmentDate", "Appointment must be scheduled for a future time.");
            }
            return  normalizedTime;
        } catch (Exception e) {
            errorMap.put("appointmentDate", "Invalid date format. Expected: 17 April 2026, 10:30 am");
            return null;
        }
    }

    /**
     * Rounds a given datetime to the nearest 10-minute boundary.
     * Examples:
     *   10:07 → 10:10
     *   10:04 → 10:00
     *   10:05 → 10:10  (rounds up on exact half)
     */
    public static ZonedDateTime roundToNearestTenMinutes(ZonedDateTime dateTime) {
        int minute = dateTime.getMinute();
        int roundedMinute = (int) (Math.round(minute / 10.0) * 10);

        if (roundedMinute == 60) {
            // e.g. 10:55 rounds to 11:00
            return dateTime.withMinute(0).withSecond(0).withNano(0).plusHours(1);
        }

        return dateTime.withMinute(roundedMinute).withSecond(0).withNano(0);
    }


    private Helpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
