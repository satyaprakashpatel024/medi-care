package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("Server is Healthy and running.")
                        .status(HttpStatus.OK)
                        .data(OffsetDateTime.now().toString())
                        .success(true)
                        .build()
        );
    }
}
