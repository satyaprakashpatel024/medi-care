package com.care.medi.entity;

import com.care.medi.exception.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum AppointmentStatus {
    SCHEDULED, COMPLETED, CANCELLED, NO_SHOW;

    @JsonCreator
    public static AppointmentStatus fromString(String value) {
        try {
            return AppointmentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException(STR."Invalid status: \{value}");
        }
    }
}
