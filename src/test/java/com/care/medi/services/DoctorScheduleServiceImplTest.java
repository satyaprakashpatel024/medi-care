package com.care.medi.services;

import com.care.medi.dtos.request.DoctorScheduleRequestDTO;
import com.care.medi.dtos.response.DoctorScheduleResponseDTO;
import com.care.medi.entity.Doctor;
import com.care.medi.entity.DoctorSchedule;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.DoctorScheduleRepository;
import com.care.medi.repository.HospitalRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorScheduleService Unit Tests")
class DoctorScheduleServiceImplTest {

  @Mock
  private DoctorScheduleRepository doctorScheduleRepository;
  @Mock
  private DoctorRepository doctorRepository;
  @Mock
  private HospitalRepository hospitalRepository;

  @InjectMocks
  private DoctorScheduleServiceImpl doctorScheduleService;

  private Doctor testDoctor;

  @BeforeEach
  @SuppressWarnings("unused")
  void setUp() {
    testDoctor = new Doctor();
    testDoctor.setId(1L);
    testDoctor.setHospitalId(1L);
    testDoctor.setFirstName("John");
    testDoctor.setLastName("Doe");
    testDoctor.setActive(true);
  }

  @Test
  @DisplayName("Should create or update schedule successfully")
  void testCreateOrUpdateSchedule_Success() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(17, 0))
      .breakStartTime(LocalTime.of(13, 0))
      .breakEndTime(LocalTime.of(14, 0))
      .slotDurationMinutes(15)
      .workingDays(List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
      .build();

    DoctorSchedule mockSchedule = DoctorSchedule.builder()
      .id(1L)
      .doctorId(1L)
      .hospitalId(1L)
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(17, 0))
      .breakStartTime(LocalTime.of(13, 0))
      .breakEndTime(LocalTime.of(14, 0))
      .slotDurationMinutes(15)
      .workingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
      .isActive(true)
      .build();

    when(doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.empty());
    when(doctorScheduleRepository.save(any(DoctorSchedule.class))).thenReturn(mockSchedule);

    DoctorScheduleResponseDTO response = doctorScheduleService.createOrUpdateSchedule(1L, 1L, request);

    assertNotNull(response);
    assertEquals(15, response.slotDurationMinutes());
    assertEquals(LocalTime.of(9, 0), response.workStartTime());
    verify(doctorScheduleRepository).save(any(DoctorSchedule.class));
  }

  @Test
  @DisplayName("Should throw exception when start time is after end time")
  void testCreateOrUpdateSchedule_InvalidTimes() {
    // Arrange
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(18, 0))
      .workEndTime(LocalTime.of(9, 0))
      .build();

    InvalidRequestException exception = assertThrows(InvalidRequestException.class,
      () -> doctorScheduleService.createOrUpdateSchedule(1L, 1L, request));

    assertEquals("Work start time must be strictly before work end time.", exception.getMessage());
  }

  @Test
  @DisplayName("Should return default schedule when no explicit schedule found")
  void testGetDoctorScheduleEntityOrDefault_Default() {
    when(doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.empty());

    DoctorSchedule result = doctorScheduleService.getDoctorScheduleEntityOrDefault(1L, 1L);

    assertNotNull(result);
    assertEquals(15, result.getSlotDurationMinutes());
    assertEquals(LocalTime.of(9, 0), result.getWorkStartTime());
    assertEquals(LocalTime.of(17, 0), result.getWorkEndTime());
  }

  @Test
  @DisplayName("Should throw ResourceNotFoundException when hospital does not exist")
  void testCreateOrUpdateSchedule_HospitalNotFound() {
    when(hospitalRepository.existsById(1L)).thenReturn(false);

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(17, 0))
      .build();

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> doctorScheduleService.createOrUpdateSchedule(1L, 1L, request));
    Assertions.assertTrue(exception.getMessage().contains("Hospital not found with ID: 1"));
  }

  // ── createOrUpdateSchedule — break start after break end ─────────────────

  @Test
  @DisplayName("Should throw exception when break start is after break end")
  void testCreateOrUpdateSchedule_BreakStartAfterBreakEnd() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(17, 0))
      .breakStartTime(LocalTime.of(14, 0))
      .breakEndTime(LocalTime.of(13, 0))
      .build();

    InvalidRequestException exception = assertThrows(InvalidRequestException.class,
      () -> doctorScheduleService.createOrUpdateSchedule(1L, 1L, request));
    Assertions.assertEquals("Break start time must be before break end time.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw exception when break is outside working hours")
  void testCreateOrUpdateSchedule_BreakOutsideWorkHours() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(17, 0))
      .breakStartTime(LocalTime.of(8, 0))
      .breakEndTime(LocalTime.of(10, 0))
      .build();

    InvalidRequestException exception = assertThrows(InvalidRequestException.class,
      () -> doctorScheduleService.createOrUpdateSchedule(1L, 1L, request));
    assertEquals("Break time must be within working hours.", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw exception when doctor not found")
  void testCreateOrUpdateSchedule_DoctorNotFound() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.empty());

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(17, 0))
      .build();

    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
      () -> doctorScheduleService.createOrUpdateSchedule(1L, 1L, request));
    assertEquals("Doctor not found with ID: 1 and Hospital ID: 1", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw exception when start time equals end time")
  void testCreateOrUpdateSchedule_EqualStartAndEndTime() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(9, 0))
      .workEndTime(LocalTime.of(9, 0))
      .build();

    InvalidRequestException exception = assertThrows(InvalidRequestException.class,
      () -> doctorScheduleService.createOrUpdateSchedule(1L, 1L, request));
    assertEquals("Work start time must be strictly before work end time.", exception.getMessage());
  }

  @Test
  @DisplayName("Should update existing schedule instead of creating new")
  void testCreateOrUpdateSchedule_ExistingScheduleUpdate() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorSchedule existingSchedule = DoctorSchedule.builder()
      .id(5L).doctorId(1L).hospitalId(1L)
      .workStartTime(LocalTime.of(9, 0)).workEndTime(LocalTime.of(17, 0))
      .slotDurationMinutes(15).workingDays("MONDAY,TUESDAY")
      .isActive(true).build();

    when(doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(1L, 1L))
      .thenReturn(Optional.of(existingSchedule));

    DoctorScheduleRequestDTO request = DoctorScheduleRequestDTO.builder()
      .workStartTime(LocalTime.of(10, 0))
      .workEndTime(LocalTime.of(18, 0))
      .slotDurationMinutes(30)
      .workingDays(List.of("MONDAY", "WEDNESDAY", "FRIDAY"))
      .build();

    when(doctorScheduleRepository.save(any(DoctorSchedule.class))).thenReturn(existingSchedule);

    DoctorScheduleResponseDTO result = doctorScheduleService.createOrUpdateSchedule(1L, 1L, request);

    assertNotNull(result);
    verify(doctorScheduleRepository).save(existingSchedule);
    assertEquals(LocalTime.of(10, 0), existingSchedule.getWorkStartTime());
  }

  // ── getScheduleByDoctorAndHospital ───────────────────────────────────────

  @Test
  @DisplayName("Should get schedule by doctor and hospital successfully")
  void testGetScheduleByDoctorAndHospital_Success() {
    when(hospitalRepository.existsById(1L)).thenReturn(true);
    when(doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(1L, 1L)).thenReturn(Optional.of(testDoctor));

    DoctorSchedule mockSchedule = DoctorSchedule.builder()
      .id(1L).doctorId(1L).hospitalId(1L)
      .workStartTime(LocalTime.of(9, 0)).workEndTime(LocalTime.of(17, 0))
      .slotDurationMinutes(15).workingDays("MONDAY,TUESDAY,WEDNESDAY")
      .isActive(true).build();
    when(doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(1L, 1L))
      .thenReturn(Optional.of(mockSchedule));

    DoctorScheduleResponseDTO result = doctorScheduleService.getScheduleByDoctorAndHospital(1L, 1L);

    assertNotNull(result);
    assertEquals(15, result.slotDurationMinutes());
  }

  // ── getDoctorScheduleEntityOrDefault — existing schedule ─────────────────

  @Test
  @DisplayName("Should return existing schedule when found")
  void testGetDoctorScheduleEntityOrDefault_Existing() {
    DoctorSchedule existingSchedule = DoctorSchedule.builder()
      .id(1L).doctorId(1L).hospitalId(1L)
      .workStartTime(LocalTime.of(8, 0)).workEndTime(LocalTime.of(16, 0))
      .slotDurationMinutes(20)
      .workingDays("MONDAY,TUESDAY")
      .isActive(true).build();
    when(doctorScheduleRepository.findByDoctorIdAndHospitalIdAndIsActiveTrue(1L, 1L))
      .thenReturn(Optional.of(existingSchedule));

    DoctorSchedule result = doctorScheduleService.getDoctorScheduleEntityOrDefault(1L, 1L);

    assertNotNull(result);
    assertEquals(20, result.getSlotDurationMinutes());
    assertEquals(LocalTime.of(8, 0), result.getWorkStartTime());
  }
}
