package com.care.medi;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class MediCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediCareApplication.class, args);
    }

    @PostConstruct
    public void init() {
        // Force the entire application to use IST
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }
}