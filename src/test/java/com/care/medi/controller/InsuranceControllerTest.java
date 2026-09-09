package com.care.medi.controller;

import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.InsuranceService;
import com.care.medi.services.UsersDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsuranceController.class)
@AutoConfigureMockMvc(addFilters = false)
public class InsuranceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InsuranceService insuranceService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Test
    @DisplayName("Should get insurance by policy number successfully")
    void testGetInsuranceByPolicyNumber() throws Exception {
        InsuranceResponseDTO response = InsuranceResponseDTO.builder()
                .id(1L)
                .policyNumber("POL123456")
                .providerName("HealthCare Life")
                .coverageAmount(500000.0)
                .build();

        when(insuranceService.getInsuranceByPolicyNumber("POL123456")).thenReturn(response);

        mockMvc.perform(get("/api/v1/insurances/POL123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Insurance retrieved successfully for this policy number."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.policyNumber").value("POL123456"))
                .andExpect(jsonPath("$.data.providerName").value("HealthCare Life"));
    }
}
