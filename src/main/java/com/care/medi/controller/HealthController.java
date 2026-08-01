package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        String dateStr = OffsetDateTime.now(Constants.ZONE_ID).format(Constants.HUMAN_DATETIME_FORMAT);
        String code = passwordEncoder.encode("Password@123");
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Server is Healthy and running.")
                        .status(HttpStatus.OK)
                        .data("Server is Healthy and running. Current Date: " + dateStr + " | Sample Password Hash: " + code)
                        .success(true)
                        .build()
        );
    }
}
