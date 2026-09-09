package com.care.medi.controller;


import com.care.medi.dtos.request.StaffRequestDTO;
import com.care.medi.dtos.request.StaffUpdateRequestDTO;
import com.care.medi.dtos.response.StaffResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.StaffService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StaffController.class)
@AutoConfigureMockMvc(addFilters = false)
public class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffService staffService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private StaffResponseDTO staffResponseDTO;

    @BeforeEach
    void setUp() {
        staffResponseDTO = StaffResponseDTO.builder().id(1L).build();
    }

    @Test
    @DisplayName("Should create staff")
    void testCreateStaff() throws Exception {
        StaffRequestDTO request = new StaffRequestDTO();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setEmail("alice.smith@example.com");
        request.setPhone("9876543210");
        request.setGender("FEMALE");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 5, 15));
        request.setHospitalId(1);

        when(staffService.createStaff(eq(1L), any(StaffRequestDTO.class))).thenReturn(staffResponseDTO);

        mockMvc.perform(post("/api/v1/admin/staff")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get all staff")
    void testGetAllStaff() throws Exception {
        Page<StaffResponseDTO> page = new PageImpl<>(Collections.singletonList(staffResponseDTO));
        when(staffService.getAllStaff(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get staff by hospital")
    void testGetStaffByHospital() throws Exception {
        Page<StaffResponseDTO> page = new PageImpl<>(Collections.singletonList(staffResponseDTO));
        when(staffService.getStaffByHospital(anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/staff/hospital/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get staff by id")
    void testGetStaffById() throws Exception {
        when(staffService.getStaffById(1L)).thenReturn(staffResponseDTO);

        mockMvc.perform(get("/api/v1/admin/staff/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update staff")
    void testUpdateStaff() throws Exception {
        StaffUpdateRequestDTO request = new StaffUpdateRequestDTO();
        request.setFirstName("Alice");

        when(staffService.updateStaff(eq(1L), any(StaffUpdateRequestDTO.class))).thenReturn(staffResponseDTO);

        mockMvc.perform(put("/api/v1/admin/staff/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should delete staff")
    void testDeleteStaff() throws Exception {
        doNothing().when(staffService).deleteStaff(1L);

        mockMvc.perform(delete("/api/v1/admin/staff/1"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Staff member deleted successfully"));
    }
}
