package com.care.medi.entity;

import com.care.medi.exception.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    NO_SHOW;

    @JsonCreator
    public static AppointmentStatus fromString(String value) {
        try {
            return AppointmentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.error("Invalid appointment status [{}]: {}", value, e.getMessage(), e);
            throw new InvalidRequestException(String.format("Invalid status: %s", value));
        }
    }
}
