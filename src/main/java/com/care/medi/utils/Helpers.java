package com.care.medi.utils;

import java.time.*;

public class Helpers {
    public static ZonedDateTime getStartOfTheDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.of(Constants.TIME_ZONE));
    }

    public static ZonedDateTime getEndOfTheDay(LocalDate date) {
        return date.atTime(LocalTime.MAX).atZone(ZoneId.of(Constants.TIME_ZONE));
    }

    private Helpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
