package com.care.medi.controller;

import com.care.medi.dtos.response.HospitalDepartmentResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.HospitalDepartmentService;
import com.care.medi.services.UsersDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminHospitalDepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminHospitalDepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospitalDepartmentService hospitalDepartmentService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Test
    @DisplayName("Should list all hospital department mappings")
    void testGetAllHospitalDepartments() throws Exception {
        HospitalDepartmentResponseDTO response = HospitalDepartmentResponseDTO.builder()
                .id(1L)
                .headDoctorName("City Hospital")
                .headDoctorId(2L)
                .departmentName("Cardiology")
                .build();

        when(hospitalDepartmentService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/admin/hospitals/departments")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hospital departments retrieved successfully"))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].headDoctorId").value(2));
    }

    @Test
    @DisplayName("Should map department to hospital")
    void testMapDepartmentToHospital() throws Exception {
        HospitalDepartmentResponseDTO response = HospitalDepartmentResponseDTO.builder()
                .id(1L)
                .headDoctorName("City Hospital")
                .headDoctorId(2L)
                .departmentName("Cardiology")
                .build();

        when(hospitalDepartmentService.mapDepartmentToHospital(1L, 2L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/admin/hospitals/1/departments/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Department mapped to hospital successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.headDoctorId").value(2));
    }

    @Test
    @DisplayName("Should unmap department from hospital")
    void testUnmapDepartmentFromHospital() throws Exception {
        doNothing().when(hospitalDepartmentService).unmapDepartmentFromHospital(1L, 2L);

        mockMvc.perform(delete("/api/v1/admin/hospitals/1/departments/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Department unmapped from hospital successfully"));
    }
}
