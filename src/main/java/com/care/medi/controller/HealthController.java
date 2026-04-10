package com.care.medi.controller;

import com.care.medi.dtos.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {
    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .message("OK")
                        .status(HttpStatus.OK)
                        .data("Server is Healthy and running.")
                        .success(true)
                        .build()
        );
    }
}
