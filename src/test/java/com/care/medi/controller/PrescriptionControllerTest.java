package com.care.medi.controller;


import com.care.medi.dtos.request.PrescriptionRequestDTO;
import com.care.medi.dtos.response.PrescriptionResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.PrescriptionServiceImpl;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrescriptionController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PrescriptionServiceImpl prescriptionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PrescriptionResponseDTO prescriptionResponseDTO;

    @BeforeEach
    void setUp() {
        prescriptionResponseDTO = PrescriptionResponseDTO.builder().id(1L).build();
    }

    @Test
    @DisplayName("Should get prescription by patient id")
    void testGetPrescriptionByPatientId() throws Exception {
        Page<PrescriptionResponseDTO> page = new PageImpl<>(Collections.singletonList(prescriptionResponseDTO));
        when(prescriptionService.getPrescriptionByPatientId(anyLong(), anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/prescriptions/patient/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get prescription by appointment id")
    void testGetPrescriptionByAppointmentId() throws Exception {
        Page<PrescriptionResponseDTO> page = new PageImpl<>(Collections.singletonList(prescriptionResponseDTO));
        when(prescriptionService.getPrescriptionByAppointmentId(anyLong(), anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/prescriptions/appointment/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should assign prescription")
    void testAssignPrescription() throws Exception {
        PrescriptionRequestDTO request = new PrescriptionRequestDTO();
        request.setPatientId(1L);
        request.setAppointmentId(1L);
        request.setDoctorId(1L);
        request.setMedications("Paracetamol");
        request.setDosageInstructions("500mg, 5 days, After meals");

        when(prescriptionService.assignPrescriptionToAppointment(eq(1L), any(PrescriptionRequestDTO.class))).thenReturn(prescriptionResponseDTO);

        mockMvc.perform(post("/api/v1/prescriptions")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
