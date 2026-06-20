package com.care.medi.entity;

import com.care.medi.exception.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED,
    NO_SHOW;

    private static final Logger logger = LoggerFactory.getLogger(AppointmentStatus.class);

    @JsonCreator
    public static AppointmentStatus fromString(String value) {
        try {
            return AppointmentStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.error("Invalid appointment status: {}", value, e);
            throw new InvalidRequestException(String.format("Invalid status: %s", value));
        }
    }
}
