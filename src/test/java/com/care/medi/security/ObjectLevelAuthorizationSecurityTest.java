package com.care.medi.security;

import com.care.medi.config.SecurityConfiguration;
import com.care.medi.controller.DoctorController;
import com.care.medi.controller.PatientController;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.dtos.response.PatientResponseDTO;
import com.care.medi.services.DoctorServiceImpl;
import com.care.medi.services.PatientServiceImpl;
import com.care.medi.services.UsersDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {DoctorController.class, PatientController.class})
@Import({SecurityConfiguration.class, AopAutoConfiguration.class})
@EnableMethodSecurity
public class ObjectLevelAuthorizationSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private DoctorServiceImpl doctorService;

    @MockitoBean
    private PatientServiceImpl patientService;

    @MockitoBean(name = "userSecurity")
    private UserSecurity userSecurity;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsersDetailsService usersDetailsService;

    @BeforeEach
    void setup() throws Exception {
        org.mockito.Mockito.doAnswer(invocation -> {
            jakarta.servlet.ServletRequest request = invocation.getArgument(0);
            jakarta.servlet.ServletResponse response = invocation.getArgument(1);
            jakarta.servlet.FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Doctor A requesting Doctor B's endpoint should return HTTP 403 Forbidden")
    @WithMockUser(username = "doctorA@hospital.com", roles = {"DOCTOR"})
    void testDoctorA_RequestingDoctorB_Returns403() throws Exception {
        when(userSecurity.isSelfDoctor(eq(2L), any())).thenReturn(false);

        mockMvc.perform(get("/api/v1/doctors/2")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Doctor A requesting Doctor A's endpoint should return HTTP 200 OK")
    @WithMockUser(username = "doctorA@hospital.com", roles = {"DOCTOR"})
    void testDoctorA_RequestingDoctorA_Returns200() throws Exception {
        when(userSecurity.isSelfDoctor(eq(1L), any())).thenReturn(true);
        when(doctorService.getDoctorByIdAndHospital(1L, 1L))
                .thenReturn(DoctorResponseDTO.builder().id(1L).build());

        mockMvc.perform(get("/api/v1/doctors/1")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Patient A requesting Patient B's endpoint should return HTTP 403 Forbidden")
    @WithMockUser(username = "patientA@gmail.com", roles = {"PATIENT"})
    void testPatientA_RequestingPatientB_Returns403() throws Exception {
        when(userSecurity.isSelfPatient(eq(2L), any())).thenReturn(false);

        mockMvc.perform(get("/api/v1/patients/2")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin requesting any endpoint should return HTTP 200 OK")
    @WithMockUser(username = "admin@hospital.com", roles = {"ADMIN"})
    void testAdmin_RequestingAnyEndpoint_Returns200() throws Exception {
        when(doctorService.getDoctorByIdAndHospital(2L, 1L))
                .thenReturn(DoctorResponseDTO.builder().id(2L).build());
        when(patientService.getPatientByIdAndHospitalId(1L, 2L))
                .thenReturn(PatientResponseDTO.builder().id(2L).build());

        mockMvc.perform(get("/api/v1/doctors/2")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/patients/2")
                        .requestAttr("X-Hospital-Id", 1L))
                .andExpect(status().isOk());
    }
}
