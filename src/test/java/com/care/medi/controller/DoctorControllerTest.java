package com.care.medi.controller;


import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.DoctorServiceImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple controller testing
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorServiceImpl doctorService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private DoctorResponseDTO doctorResponseDTO;
    private DoctorListResponseDTO doctorListResponseDTO;

    @BeforeEach
    void setUp() {
        doctorResponseDTO = DoctorResponseDTO.builder().id(1L).build();
        doctorListResponseDTO = DoctorListResponseDTO.builder().id(1L).build();
    }

    @Test
    @DisplayName("Should get all active doctors")
    void testGetAllActiveDoctors() throws Exception {
        Page<DoctorListResponseDTO> page = new PageImpl<>(Collections.singletonList(doctorListResponseDTO));
        when(doctorService.getAllActiveDoctors(anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/doctors/all")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All Doctors fetched successfully "))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get active doctor by id and hospital")
    void testGetActiveDoctorByIdAndHospital() throws Exception {
        when(doctorService.getDoctorByIdAndHospital(1L, 1L)).thenReturn(doctorResponseDTO);

        mockMvc.perform(get("/api/v1/doctors/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should get all active doctors by hospital")
    void testGetAllActiveDoctorsByHospital() throws Exception {
        Page<DoctorListResponseDTO> page = new PageImpl<>(Collections.singletonList(doctorListResponseDTO));
        when(doctorService.getAllActiveDoctorsByHospital(anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/doctors")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should create doctor in hospital")
    void testCreateDoctorInHospital() throws Exception {
        DoctorRequestDTO request = new DoctorRequestDTO();
        request.setFirstName("Jonathan");
        request.setLastName("Douglas");
        request.setEmail("john@example.com");
        request.setPhone("9876543210");
        request.setSpeciality("Cardiology");
        request.setDateOfBirth(LocalDate.of(1985, 3, 15));
        request.setHospitalId(1L);
        request.setDepartmentId(1L);

        when(doctorService.createDoctorInHospital(eq(1L), any(DoctorRequestDTO.class))).thenReturn(doctorResponseDTO);

        mockMvc.perform(post("/api/v1/doctors")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should update doctor")
    void testUpdateDoctor() throws Exception {
        DoctorUpdateRequestDTO request = new DoctorUpdateRequestDTO();

        when(doctorService.updateDoctorByIdAndHospital(eq(1L), eq(1L), any(DoctorUpdateRequestDTO.class))).thenReturn(doctorResponseDTO);

        mockMvc.perform(put("/api/v1/doctors/1")
                        .requestAttr("X-Hospital-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("Should delete doctor by id and hospital")
    void testDeleteDoctorByIdAndHospital() throws Exception {
        doNothing().when(doctorService).deleteDoctorByIdAndHospital(1L, 1L);

        mockMvc.perform(delete("/api/v1/doctors/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Doctor deleted successfully..."));
    }

    @Test
    @DisplayName("Should get all appointments by doctor and hospital")
    void testGetAllAppointmentsByDoctorAndHospital() throws Exception {
        Page<AppointmentListResponseDTO> page = new PageImpl<>(Collections.emptyList());
        when(doctorService.getAppointmentsByDoctorAndHospitalAndDate(anyLong(), anyLong(), any(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/doctors/1/appointments")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get doctors by department and hospital")
    void testGetDoctorsByDepartmentAndHospital() throws Exception {
        Page<DoctorListResponseDTO> page = new PageImpl<>(Collections.singletonList(doctorListResponseDTO));
        when(doctorService.getActiveDoctorsByDepartmentAndHospital(anyLong(), anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/doctors/department/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    @DisplayName("Should get active doctor by speciality and hospital")
    void testGetActiveDoctorBySpecialityAndHospital() throws Exception {
        Page<DoctorListResponseDTO> page = new PageImpl<>(Collections.singletonList(doctorListResponseDTO));
        when(doctorService.getActiveDoctorsBySpecialityAndHospital(anyString(), anyLong(), anyInt(), anyInt(), anyString())).thenReturn(page);

        mockMvc.perform(get("/api/v1/doctors/speciality")
                        .param("speciality", "Cardiology")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }
}
