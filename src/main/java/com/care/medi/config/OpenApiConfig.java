package com.care.medi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mediCareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medi-Care Enterprise Backend API")
                        .description("Comprehensive REST API for Hospital Infrastructure, Appointment Scheduling, Electronic Health Records (EHR), and User Security Management.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Medi-Care Engineering")
                                .email("engineering@medi-care.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://medi-care.com/terms")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your valid JWT access token (without 'Bearer ' prefix).")));
    }
}
