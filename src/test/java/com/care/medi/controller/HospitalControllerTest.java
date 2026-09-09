package com.care.medi.controller;

import com.care.medi.dtos.request.HospitalAddressRequestDTO;
import com.care.medi.dtos.request.HospitalRequestDTO;
import com.care.medi.dtos.request.HospitalUpdateRequestDTO;
import com.care.medi.dtos.response.HospitalListResponseDTO;
import com.care.medi.dtos.response.HospitalResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.HospitalServiceImpl;
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

@WebMvcTest(HospitalController.class)
@AutoConfigureMockMvc(addFilters = false)
public class HospitalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HospitalServiceImpl hospitalService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private HospitalResponseDTO hospitalResponseDTO;
    private HospitalListResponseDTO hospitalListResponseDTO;

    @BeforeEach
    void setUp() {
        hospitalResponseDTO = HospitalResponseDTO.builder().id(1L).name("City Hospital").phone("9876543210").build();
        hospitalListResponseDTO = HospitalListResponseDTO.builder().id(1L).name("City Hospital").phone("9876543210").build();
    }

    @Test
    @DisplayName("Should get all hospitals paginated")
    void testGetAllHospitals() throws Exception {
        Page<HospitalListResponseDTO> page = new PageImpl<>(Collections.singletonList(hospitalListResponseDTO));
        when(hospitalService.getAllHospitals(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/hospitals")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Successfully retrieved All Hospital list."))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get hospital by ID")
    void testGetHospitalById() throws Exception {
        when(hospitalService.getHospitalById(1L)).thenReturn(hospitalResponseDTO);

        mockMvc.perform(get("/api/v1/hospitals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hospital fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should create hospital")
    void testCreateHospital() throws Exception {
        HospitalAddressRequestDTO address = HospitalAddressRequestDTO.builder()
                .phoneNumber("9876543210")
                .addressLine1("123 Main St")
                .city("Metropolis")
                .state("State")
                .postalCode("123456")
                .country("India")
                .build();

        HospitalRequestDTO request = HospitalRequestDTO.builder()
                .name("City Hospital")
                .phone("9876543210")
                .address(address)
                .build();

        when(hospitalService.createHospital(any(HospitalRequestDTO.class))).thenReturn(hospitalResponseDTO);

        mockMvc.perform(post("/api/v1/hospitals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Hospital created successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update hospital")
    void testUpdateHospital() throws Exception {
        HospitalUpdateRequestDTO request = HospitalUpdateRequestDTO.builder()
                .name("City Hospital Updated")
                .phone("9876543210")
                .build();

        when(hospitalService.updateHospital(eq(1L), any(HospitalUpdateRequestDTO.class))).thenReturn(hospitalResponseDTO);

        mockMvc.perform(put("/api/v1/hospitals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Hospital updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
