package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

/**
 * Controller for application health monitoring and diagnostic checks.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Evaluates server availability and returns the current timestamp.
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} with server diagnostic information
     */
    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        String dateStr = OffsetDateTime.now(Constants.ZONE_ID).format(Constants.HUMAN_DATETIME_FORMAT);
        String message = "Server is Healthy and running.";
        String data = "Server is Healthy and running. Current Date: " + dateStr;
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }
}