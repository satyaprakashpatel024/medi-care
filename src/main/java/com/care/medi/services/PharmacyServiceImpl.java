package com.care.medi.services;

import com.care.medi.dtos.request.DispenseItemRequestDTO;
import com.care.medi.dtos.request.DispenseRequestDTO;
import com.care.medi.dtos.request.MedicationRequestDTO;
import com.care.medi.dtos.response.DispenseResponseDTO;
import com.care.medi.dtos.response.MedicationResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.InvalidRequestException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PharmacyServiceImpl implements PharmacyService {

  private final MedicationRepository medicationRepository;
  private final DispenseRecordRepository dispenseRecordRepository;
  private final PatientRepository patientRepository;
  private final StaffRepository staffRepository;
  private final PrescriptionRepository prescriptionRepository;

  @Override
  @Transactional
  public MedicationResponseDTO addMedication(MedicationRequestDTO request) {
    Medication medication = Medication.builder()
      .name(request.getName())
      .manufacturer(request.getManufacturer())
      .dosageForm(request.getDosageForm())
      .unitPrice(request.getUnitPrice())
      .stockQuantity(request.getStockQuantity())
      .reorderLevel(request.getReorderLevel())
      .expiryDate(request.getExpiryDate())
      .batchNumber(request.getBatchNumber())
      .build();
    return MedicationResponseDTO.toResponse(medicationRepository.save(medication));
  }

  @Override
  @Transactional
  public MedicationResponseDTO updateMedication(Long id, MedicationRequestDTO request) {
    Medication medication = medicationRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Medication not found"));
    medication.setName(request.getName());
    medication.setManufacturer(request.getManufacturer());
    medication.setDosageForm(request.getDosageForm());
    medication.setUnitPrice(request.getUnitPrice());
    medication.setStockQuantity(request.getStockQuantity());
    medication.setReorderLevel(request.getReorderLevel());
    medication.setExpiryDate(request.getExpiryDate());
    medication.setBatchNumber(request.getBatchNumber());
    return MedicationResponseDTO.toResponse(medicationRepository.save(medication));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<MedicationResponseDTO> getAllMedications(int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return medicationRepository.findAll(pageable).map(MedicationResponseDTO::toResponse);
  }

  @Override
  @Transactional(readOnly = true)
  public List<MedicationResponseDTO> getLowStockMedications(Integer threshold) {
    return medicationRepository.findByStockQuantityLessThanEqual(threshold)
      .stream().map(MedicationResponseDTO::toResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public DispenseResponseDTO dispenseMedications(DispenseRequestDTO request) {
    Patient patient = patientRepository.findById(request.getPatientId())
      .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

    Staff staff = staffRepository.findById(request.getDispensedById())
      .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

    Prescription prescription = null;
    if (request.getPrescriptionId() != null) {
      prescription = prescriptionRepository.findById(request.getPrescriptionId())
        .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
    }

    DispenseRecord record = DispenseRecord.builder()
      .patient(patient)
      .prescription(prescription)
      .dispensedBy(staff)
      .dispenseStatus(DispenseStatus.DISPENSED) // Assuming default is dispensed if requested
      .paymentStatus(request.getPaymentStatus())
      .dispenseDate(ZonedDateTime.now())
      .totalAmount(0.0)
      .build();

    double totalAmount = 0.0;

    for (DispenseItemRequestDTO itemReq : request.getItems()) {
      Medication medication = medicationRepository.findById(itemReq.getMedicationId())
        .orElseThrow(() -> new ResourceNotFoundException("Medication not found: " + itemReq.getMedicationId()));

      if (medication.getStockQuantity() < itemReq.getQuantity()) {
        throw new InvalidRequestException("Insufficient stock for medication: " + medication.getName());
      }

      // Reduce stock
      medication.setStockQuantity(medication.getStockQuantity() - itemReq.getQuantity());
      medicationRepository.save(medication);

      double subTotal = itemReq.getQuantity() * medication.getUnitPrice();
      totalAmount += subTotal;

      DispenseItem item = DispenseItem.builder()
        .dispenseRecord(record)
        .medication(medication)
        .quantity(itemReq.getQuantity())
        .unitPrice(medication.getUnitPrice())
        .subTotal(subTotal)
        .build();

      record.getItems().add(item);
    }

    record.setTotalAmount(totalAmount);
    DispenseRecord savedRecord = dispenseRecordRepository.save(record);

    return DispenseResponseDTO.toResponse(savedRecord);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<DispenseResponseDTO> getPatientDispenseHistory(Long patientId, int page, int size, String sortBy) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    return dispenseRecordRepository.findByPatientId(patientId, pageable).map(DispenseResponseDTO::toResponse);
  }
}
