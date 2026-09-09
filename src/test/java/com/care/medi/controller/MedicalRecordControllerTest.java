package com.care.medi.controller;

import com.care.medi.dtos.request.MedicalRecordRequestDTO;
import com.care.medi.dtos.request.MedicalRecordUpdateRequestDTO;
import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.RecordStatus;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.MedicalRecordService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicalRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @org.springframework.test.context.bean.override.mockito.MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private MedicalRecordResponseDTO recordResponseDTO;
    private MedicalRecordListResponseDTO recordListResponseDTO;

    @BeforeEach
    void setUp() {
        recordResponseDTO = MedicalRecordResponseDTO.builder().id(1L).build();
        recordListResponseDTO = MedicalRecordListResponseDTO.builder().id(1L).build();
    }

    @Test
    @DisplayName("Should create a medical record")
    void testCreateRecord() throws Exception {
        MedicalRecordRequestDTO request = new MedicalRecordRequestDTO();
        request.setPatientId(1L);
        request.setDoctorId(1L);
        request.setDiagnosis("Acute upper respiratory infection");
        request.setRecordDate(java.time.LocalDate.of(2024, 6, 15));

        when(medicalRecordService.createRecord(eq(1L), any(MedicalRecordRequestDTO.class))).thenReturn(recordResponseDTO);

        mockMvc.perform(post("/api/v1/medical-records")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get a record by id")
    void testGetRecordById() throws Exception {
        when(medicalRecordService.getRecordById(1L, 1L)).thenReturn(recordResponseDTO);

        mockMvc.perform(get("/api/v1/medical-records/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get a record by appointment id")
    void testGetRecordByAppointmentId() throws Exception {
        when(medicalRecordService.getRecordByAppointmentId(1L, 1L)).thenReturn(recordResponseDTO);

        mockMvc.perform(get("/api/v1/medical-records/appointment/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get records by patient")
    void testGetRecordsByPatient() throws Exception {
        Page<MedicalRecordListResponseDTO> page = new PageImpl<>(Collections.singletonList(recordListResponseDTO));
        when(medicalRecordService.getRecordsByPatient(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/medical-records/patient/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get active records by patient")
    void testGetActiveRecordsByPatient() throws Exception {
        Page<MedicalRecordListResponseDTO> page = new PageImpl<>(Collections.singletonList(recordListResponseDTO));
        when(medicalRecordService.getActiveRecordsByPatient(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/medical-records/patient/1/active")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get latest record by patient")
    void testGetLatestRecordByPatient() throws Exception {
        when(medicalRecordService.getLatestRecordByPatient(1L, 1L)).thenReturn(recordResponseDTO);

        mockMvc.perform(get("/api/v1/medical-records/patient/1/latest")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get records by doctor")
    void testGetRecordsByDoctor() throws Exception {
        Page<MedicalRecordListResponseDTO> page = new PageImpl<>(Collections.singletonList(recordListResponseDTO));
        when(medicalRecordService.getRecordsByDoctor(eq(1L), eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/medical-records/doctor/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get records by hospital")
    void testGetRecordsByHospital() throws Exception {
        Page<MedicalRecordListResponseDTO> page = new PageImpl<>(Collections.singletonList(recordListResponseDTO));
        when(medicalRecordService.getRecordsByHospital(eq(1L), any(RecordStatus.class), any(LocalDate.class), any(LocalDate.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/medical-records/hospital")
                        .requestAttr("X-Hospital-Id", 1L)
                        .param("status", "ACTIVE")
                        .param("from", "2024-01-01")
                        .param("to", "2024-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should update medical record")
    void testUpdateRecord() throws Exception {
        MedicalRecordUpdateRequestDTO request = new MedicalRecordUpdateRequestDTO();

        when(medicalRecordService.updateRecord(eq(1L), eq(1L), any(MedicalRecordUpdateRequestDTO.class))).thenReturn(recordResponseDTO);

        mockMvc.perform(put("/api/v1/medical-records/1")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should delete medical record")
    void testDeleteRecord() throws Exception {
        when(medicalRecordService.deleteRecord(1L, 1L)).thenReturn("Success");

        mockMvc.perform(delete("/api/v1/medical-records/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Success"));
    }
}
