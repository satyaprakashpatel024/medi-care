package com.care.medi.controller;

import com.care.medi.dtos.request.DispenseItemRequestDTO;
import com.care.medi.dtos.request.DispenseRequestDTO;
import com.care.medi.dtos.request.MedicationRequestDTO;
import com.care.medi.dtos.response.DispenseResponseDTO;
import com.care.medi.dtos.response.MedicationResponseDTO;
import com.care.medi.entity.PaymentStatus;
import com.care.medi.security.JwtAuthenticationFilter;
import com.care.medi.security.JwtService;
import com.care.medi.services.PharmacyService;
import com.care.medi.services.UsersDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PharmacyController.class)
@AutoConfigureMockMvc(addFilters = false)
class PharmacyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PharmacyService pharmacyService;

  @MockitoBean
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @MockitoBean
  private JwtService jwtService;

  @MockitoBean
  private UsersDetailsService usersDetailsService;

  @Autowired
  private ObjectMapper objectMapper;

  private MedicationRequestDTO medReq;
  private DispenseRequestDTO dispReq;

  @BeforeEach
  void setUp() {
    medReq = new MedicationRequestDTO();
    medReq.setName("Aspirin");
    medReq.setUnitPrice(10.0);
    medReq.setStockQuantity(100);
    medReq.setReorderLevel(10);

    DispenseItemRequestDTO item = new DispenseItemRequestDTO();
    item.setMedicationId(1L);
    item.setQuantity(5);

    dispReq = new DispenseRequestDTO();
    dispReq.setPatientId(1L);
    dispReq.setDispensedById(1L);
    dispReq.setPaymentStatus(PaymentStatus.PAID);
    dispReq.setItems(Collections.singletonList(item));
  }

  @Test
  void addMedication() throws Exception {
    MedicationResponseDTO res = MedicationResponseDTO.builder().id(1L).name("Aspirin").build();
    when(pharmacyService.addMedication(any())).thenReturn(res);

    mockMvc.perform(post("/api/v1/pharmacy/medications")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(medReq)))
      .andExpect(status().isCreated());
  }

  @Test
  void dispenseMedications() throws Exception {
    DispenseResponseDTO res = DispenseResponseDTO.builder().id(1L).totalAmount(50.0).build();
    when(pharmacyService.dispenseMedications(any())).thenReturn(res);

    mockMvc.perform(post("/api/v1/pharmacy/dispense")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dispReq)))
      .andExpect(status().isCreated());
  }

  @Test
  void getAllMedications() throws Exception {
    Page<MedicationResponseDTO> page = new PageImpl<>(Collections.emptyList());
    when(pharmacyService.getAllMedications(anyInt(), anyInt(), any())).thenReturn(page);
    mockMvc.perform(get("/api/v1/pharmacy/medications"))
      .andExpect(status().isOk());
  }
}
