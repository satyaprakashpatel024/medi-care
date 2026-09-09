package com.care.medi.controller;


import com.care.medi.dtos.request.AppointmentRequestDTO;
import com.care.medi.dtos.request.AppointmentRescheduleDTO;
import com.care.medi.dtos.request.AppointmentUpdateRequestDTO;
import com.care.medi.dtos.response.ApiResponse;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.AppointmentResponseDTO;
import com.care.medi.dtos.response.AppointmentSummaryResponseDTO;
import com.care.medi.entity.AppointmentStatus;
import com.care.medi.services.AppointmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentServiceImpl appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    @Test
    void testGetAppointmentById() {
        AppointmentResponseDTO mockResponse = mock(AppointmentResponseDTO.class);
        when(appointmentService.getAppointmentByIdAndHospital(1L, 1L)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response = appointmentController.getAppointmentById(1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockResponse, response.getBody().data());
    }

    @Test
    void testGetAllAppointmentsByHospitalAndDate() {
        Page<AppointmentSummaryResponseDTO> mockPage = new PageImpl<>(List.of());
        when(appointmentService.getAllAppointmentsByHospitalAndDate(eq(1L), eq(0), eq(5), eq("id"), any(LocalDate.class)))
                .thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<AppointmentSummaryResponseDTO>>> response = appointmentController.getAllAppointmentsByHospitalAndDate(1L, 0, 5, "id", LocalDate.now());

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockPage, response.getBody().data());
    }

    @Test
    void testGetAppointmentByHospitalAndStatusAndDate() {
        Page<AppointmentListResponseDTO> mockPage = new PageImpl<>(List.of());
        when(appointmentService.getAppointmentsByHospitalAndStatusAndDate(eq(1L), eq(AppointmentStatus.SCHEDULED), eq(0), eq(5), eq("id"), any(LocalDate.class)))
                .thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<AppointmentListResponseDTO>>> response = appointmentController.getAppointmentByHospitalAndStatusAndDate(1L, AppointmentStatus.SCHEDULED, 0, 5, "id", LocalDate.now());

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockPage, response.getBody().data());
    }

    @Test
    void testBookAnAppointment() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        AppointmentRequestDTO dto = new AppointmentRequestDTO();
        AppointmentResponseDTO mockResponse = mock(AppointmentResponseDTO.class);
        when(mockResponse.appointmentId()).thenReturn(1L);
        when(appointmentService.createAppointment(1L, dto)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response = appointmentController.bookAnAppointment(1L, dto);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockResponse, response.getBody().data());
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void testRescheduleAppointment() {
        AppointmentRescheduleDTO dto = new AppointmentRescheduleDTO();
        AppointmentResponseDTO mockResponse = mock(AppointmentResponseDTO.class);
        when(appointmentService.rescheduleAppointment(1L, dto, 1L)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response = appointmentController.rescheduleAppointment(1L, 1L, dto);

        assertEquals(202, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockResponse, response.getBody().data());
    }

    @Test
    void testUpdateAppointment() {
        AppointmentUpdateRequestDTO dto = new AppointmentUpdateRequestDTO();
        AppointmentResponseDTO mockResponse = mock(AppointmentResponseDTO.class);
        when(appointmentService.updateAppointment(1L, 1L, dto)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response = appointmentController.updateAppointment(1L, 1L, dto);

        assertEquals(202, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockResponse, response.getBody().data());
    }

    @Test
    void testCancelAppointment() {
        AppointmentResponseDTO mockResponse = mock(AppointmentResponseDTO.class);
        when(appointmentService.getAppointmentByIdAndHospital(1L, 1L)).thenReturn(mockResponse);

        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response = appointmentController.cancelAppointment(1L, 1L);

        assertEquals(202, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockResponse, response.getBody().data());
        verify(appointmentService).cancelAppointment(1L, 1L);
    }

    @Test
    void testGetAllAppointmentsByHospitalAndPatientId() {
        Page<AppointmentResponseDTO> mockPage = new PageImpl<>(List.of());
        when(appointmentService.getAppointmentsByHospitalAndPatient(1L, 1L, 0, 5, "id")).thenReturn(mockPage);

        ResponseEntity<ApiResponse<Page<AppointmentResponseDTO>>> response = appointmentController.getAllAppointmentsByHospitalAndPatientId(1L, 1L, 0, 5, "id");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(mockPage, response.getBody().data());
    }

    @Test
    void testDeleteAppointment() {
        ResponseEntity<ApiResponse<AppointmentResponseDTO>> response = appointmentController.deleteAppointment(1L, 1L);

        assertEquals(202, response.getStatusCodeValue());
        verify(appointmentService).deleteAppointment(1L, 1L);
    }
}
