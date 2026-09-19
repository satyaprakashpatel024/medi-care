package com.care.medi.services;

import com.care.medi.dtos.request.DispenseItemRequestDTO;
import com.care.medi.dtos.request.DispenseRequestDTO;
import com.care.medi.dtos.request.MedicationRequestDTO;
import com.care.medi.dtos.response.DispenseResponseDTO;
import com.care.medi.dtos.response.MedicationResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PharmacyServiceImpl Unit Tests")
class PharmacyServiceImplTest {

  @Mock
  private MedicationRepository medicationRepository;
  @Mock
  private DispenseRecordRepository dispenseRecordRepository;
  @Mock
  private PatientRepository patientRepository;
  @Mock
  private StaffRepository staffRepository;
  @Mock
  private PrescriptionRepository prescriptionRepository;

  @InjectMocks
  private PharmacyServiceImpl pharmacyService;

  private Medication testMedication;
  private MedicationRequestDTO medReqDTO;
  private Patient testPatient;
  private Staff testStaff;
  private DispenseRequestDTO dispReqDTO;

  @BeforeEach
  void setUp() {
    testMedication = new Medication();
    testMedication.setId(1L);
    testMedication.setName("Aspirin");
    testMedication.setUnitPrice(10.0);
    testMedication.setStockQuantity(100);

    medReqDTO = new MedicationRequestDTO();
    medReqDTO.setName("Aspirin");
    medReqDTO.setUnitPrice(10.0);
    medReqDTO.setStockQuantity(100);
    medReqDTO.setReorderLevel(10);

    testPatient = new Patient();
    testPatient.setId(1L);
    testPatient.setFirstName("John");

    testStaff = new Staff();
    testStaff.setId(1L);
    testStaff.setFirstName("Jane");

    DispenseItemRequestDTO itemReq = new DispenseItemRequestDTO();
    itemReq.setMedicationId(1L);
    itemReq.setQuantity(5);

    dispReqDTO = new DispenseRequestDTO();
    dispReqDTO.setPatientId(1L);
    dispReqDTO.setDispensedById(1L);
    dispReqDTO.setPaymentStatus(PaymentStatus.PAID);
    dispReqDTO.setItems(Collections.singletonList(itemReq));
  }

  @Test
  void addMedication() {
    when(medicationRepository.save(any(Medication.class))).thenReturn(testMedication);
    MedicationResponseDTO response = pharmacyService.addMedication(medReqDTO);
    assertNotNull(response);
    assertEquals("Aspirin", response.name());
    verify(medicationRepository, times(1)).save(any(Medication.class));
  }

  @Test
  void updateMedication() {
    when(medicationRepository.findById(1L)).thenReturn(Optional.of(testMedication));
    when(medicationRepository.save(any(Medication.class))).thenReturn(testMedication);
    MedicationResponseDTO response = pharmacyService.updateMedication(1L, medReqDTO);
    assertNotNull(response);
    verify(medicationRepository, times(1)).findById(1L);
    verify(medicationRepository, times(1)).save(any(Medication.class));
  }

  @Test
  void getLowStockMedications() {
    when(medicationRepository.findByStockQuantityLessThanEqual(10)).thenReturn(Collections.singletonList(testMedication));
    List<MedicationResponseDTO> lowStock = pharmacyService.getLowStockMedications(10);
    assertEquals(1, lowStock.size());
    verify(medicationRepository, times(1)).findByStockQuantityLessThanEqual(10);
  }

  @Test
  void dispenseMedications_Success() {
    when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
    when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
    when(medicationRepository.findById(1L)).thenReturn(Optional.of(testMedication));

    DispenseRecord savedRecord = new DispenseRecord();
    savedRecord.setId(1L);
    savedRecord.setPatient(testPatient);
    savedRecord.setDispensedBy(testStaff);
    savedRecord.setDispenseStatus(DispenseStatus.DISPENSED);
    savedRecord.setPaymentStatus(PaymentStatus.PAID);
    savedRecord.setTotalAmount(50.0);

    when(dispenseRecordRepository.save(any(DispenseRecord.class))).thenReturn(savedRecord);

    DispenseResponseDTO response = pharmacyService.dispenseMedications(dispReqDTO);
    assertNotNull(response);
    assertEquals(50.0, response.totalAmount());
    assertEquals(95, testMedication.getStockQuantity());
    verify(medicationRepository, times(1)).save(testMedication);
  }

  @Test
  void dispenseMedications_InsufficientStock() {
    testMedication.setStockQuantity(2); // Requesting 5
    when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
    when(staffRepository.findById(1L)).thenReturn(Optional.of(testStaff));
    when(medicationRepository.findById(1L)).thenReturn(Optional.of(testMedication));

    assertThrows(InvalidRequestException.class, () -> pharmacyService.dispenseMedications(dispReqDTO));
  }

  @Test
  void getPatientDispenseHistory() {
    Page<DispenseRecord> page = new PageImpl<>(Collections.emptyList());
    when(dispenseRecordRepository.findByPatientId(eq(1L), any(Pageable.class))).thenReturn(page);
    Page<DispenseResponseDTO> response = pharmacyService.getPatientDispenseHistory(1L, 0, 10, "id");
    assertNotNull(response);
  }
}
