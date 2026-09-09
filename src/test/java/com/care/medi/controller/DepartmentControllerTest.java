package com.care.medi.controller;

import com.care.medi.dtos.request.DepartmentRequestDTO;
import com.care.medi.dtos.response.DepartmentResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.DepartmentService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentResponseDTO departmentResponseDTO;

    @BeforeEach
    void setUp() {
        departmentResponseDTO = DepartmentResponseDTO.builder().id(1L).name("Cardiology").build();
    }

    @Test
    @DisplayName("Should get all departments")
    void testGetAllDepartments() throws Exception {
        Page<DepartmentResponseDTO> page = new PageImpl<>(Collections.singletonList(departmentResponseDTO));
        when(departmentService.getAllDepartments(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should create department")
    void testCreateDepartment() throws Exception {
        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setName("Cardiology");
        request.setDescription("Department of Cardiology");

        when(departmentService.createDepartment(any(DepartmentRequestDTO.class))).thenReturn(departmentResponseDTO);

        mockMvc.perform(post("/api/v1/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get department by id")
    void testGetDepartmentById() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(departmentResponseDTO);

        mockMvc.perform(get("/api/v1/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update department")
    void testUpdateDepartment() throws Exception {
        DepartmentRequestDTO request = new DepartmentRequestDTO();
        request.setName("Cardiology");
        request.setDescription("Department of Cardiology");

        when(departmentService.updateDepartment(eq(1L), any(DepartmentRequestDTO.class))).thenReturn(departmentResponseDTO);

        mockMvc.perform(put("/api/v1/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
