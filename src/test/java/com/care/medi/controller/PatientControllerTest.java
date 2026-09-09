package com.care.medi.controller;


import com.care.medi.dtos.request.InsuranceRequestDTO;
import com.care.medi.dtos.request.PatientRequestDTO;
import com.care.medi.dtos.request.PatientUpdateRequestDTO;
import com.care.medi.dtos.response.InsuranceResponseDTO;
import com.care.medi.dtos.response.PatientListResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.PatientServiceImpl;
import com.care.medi.services.UsersDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientServiceImpl patientService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PatientResponseDTO patientResponseDTO;
    private PatientListResponseDTO patientListResponseDTO;
    private InsuranceResponseDTO insuranceResponseDTO;

    @BeforeEach
    void setUp() {
        patientResponseDTO = PatientResponseDTO.builder().id(1L).build();
        patientListResponseDTO = PatientListResponseDTO.builder().id(1L).build();
        insuranceResponseDTO = InsuranceResponseDTO.builder().id(1L).build();
    }

    @Test
    @DisplayName("Should get all patients by hospital")
    void testGetAllPatientsByHospital() throws Exception {
        Page<PatientListResponseDTO> page = new PageImpl<>(Collections.singletonList(patientListResponseDTO));
        when(patientService.getAllPatientsByHospital(anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/patients")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get patient by id and hospital")
    void testGetPatientByIdAndHospital() throws Exception {
        when(patientService.getPatientByIdAndHospitalId(1L, 1L)).thenReturn(patientResponseDTO);

        mockMvc.perform(get("/api/v1/patients/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should create patient in hospital")
    void testSavePatientInHospital() throws Exception {
        PatientRequestDTO request = new PatientRequestDTO();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("jane.doe@example.com");
        request.setPhone("9876543210");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setGender("FEMALE");

        when(patientService.createPatientInHospital(eq(1L), any(PatientRequestDTO.class))).thenReturn(patientResponseDTO);

        mockMvc.perform(post("/api/v1/patients")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update patient")
    void testUpdatePatient() throws Exception {
        PatientUpdateRequestDTO request = new PatientUpdateRequestDTO();

        when(patientService.updatePatientInHospital(eq(1L), eq(1L), any(PatientUpdateRequestDTO.class))).thenReturn(patientResponseDTO);

        mockMvc.perform(put("/api/v1/patients/1")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should assign insurance to patient")
    void testAssignInsuranceToPatient() throws Exception {
        InsuranceRequestDTO request = new InsuranceRequestDTO();
        request.setProviderName("HealthCo");
        request.setPolicyNumber("POL123");
        request.setPolicyType("HEALTH");
        request.setCoverageAmount(500000.0);
        request.setInsuranceStatus("ACTIVE");
        request.setStartDate(java.time.LocalDate.of(2024, 1, 1));
        request.setExpiryDate(java.time.LocalDate.of(2027, 12, 31));
        request.setProviderContactEmail("contact@healthco.com");

        when(patientService.assignInsurance(eq(1L), eq(1L), any(InsuranceRequestDTO.class))).thenReturn(insuranceResponseDTO);

        mockMvc.perform(post("/api/v1/patients/1/insurances")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get all insurances of patient")
    void testGetAllInsurancesOfPatient() throws Exception {
        List<InsuranceResponseDTO> list = Collections.singletonList(insuranceResponseDTO);
        when(patientService.getInsuranceByPatientId(1L, 1L)).thenReturn(list);

        mockMvc.perform(get("/api/v1/patients/1/insurances")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }
}
