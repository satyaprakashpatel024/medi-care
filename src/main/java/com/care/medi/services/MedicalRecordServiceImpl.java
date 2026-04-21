package com.care.medi.services;

import com.care.medi.dtos.request.MedicalRecordRequestDTO;
import com.care.medi.dtos.request.MedicalRecordUpdateRequestDTO;
import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.AppointmentRepository;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.MedicalRecordRepository;
import com.care.medi.repository.PatientRepository;
import com.care.medi.utils.MedicalRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordMapper medicalRecordMapper;

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @SneakyThrows
    @Override
    @Transactional
    public MedicalRecordResponseDTO createRecord(Long hospitalId, MedicalRecordRequestDTO request) {
        log.info("Creating medical record for patient {} by doctor {} in hospital {}",
                request.getPatientId(), request.getDoctorId(), hospitalId);

        Patient patient = findPatientInHospital(request.getPatientId(), hospitalId);
        Doctor doctor  = findDoctorInHospital(request.getDoctorId(), hospitalId);

        MedicalRecord.MedicalRecordBuilder builder = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .hospitalId(hospitalId)
                .diagnosis(request.getDiagnosis().trim())
                .symptoms(request.getSymptoms())
                .treatmentPlan(request.getTreatmentPlan())
                .followUpNotes(request.getFollowUpNotes())
                .recordDate(request.getRecordDate())
                .status(RecordStatus.ACTIVE);

        // Optional appointment link
        if (request.getAppointmentId() != null) {
            Appointment appointment = findAppointmentInHospital(
                    request.getAppointmentId(), hospitalId);

            // One record per appointment — guard against duplicates
            if (medicalRecordRepository.existsByAppointmentIdAndHospitalId(
                    appointment.getId(), hospitalId)) {
                throw new BadRequestException(
                        "A medical record already exists for appointment ID %d.".formatted(request.getAppointmentId()));
            }

            // Validate appointment belongs to the same patient
            if (!appointment.getPatient().getId().equals(request.getPatientId())) {
                throw new BadRequestException(
                        "Appointment ID %d does not belong to patient ID %d.".formatted(request.getAppointmentId(), request.getPatientId()));
            }

            builder.appointment(appointment);
        }

        MedicalRecord saved = medicalRecordRepository.save(builder.build());
        log.info("Medical record created with ID {}", saved.getId());
        return medicalRecordMapper.toResponseDTO(saved);
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getRecordById(Long id, Long hospitalId) {
        MedicalRecord record = findRecord(id, hospitalId);
        return medicalRecordMapper.toResponseDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getRecordByAppointmentId(Long appointmentId, Long hospitalId) {
        // Verify the appointment exists in this hospital first
        findAppointmentInHospital(appointmentId, hospitalId);

        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No medical record found for appointment ID %d".formatted(appointmentId)));
        return medicalRecordMapper.toResponseDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordListResponseDTO> getRecordsByPatient(
            Long patientId, Long hospitalId, Pageable pageable) {
        verifyPatientInHospital(patientId, hospitalId);
        return medicalRecordRepository
                .findAllByPatientIdAndHospitalId(patientId, hospitalId, pageable)
                .map(medicalRecordMapper::toListResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordListResponseDTO> getActiveRecordsByPatient(
            Long patientId, Long hospitalId, Pageable pageable) {
        verifyPatientInHospital(patientId, hospitalId);
        return medicalRecordRepository
                .findActiveByPatientIdAndHospitalId(patientId, hospitalId, pageable)
                .map(medicalRecordMapper::toListResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getLatestRecordByPatient(Long patientId, Long hospitalId) {
        verifyPatientInHospital(patientId, hospitalId);
        Page<MedicalRecord> page = medicalRecordRepository
                .findLatestActiveByPatientIdAndHospitalId(
                        patientId, hospitalId, PageRequest.of(0, 1));

        if (page.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No active medical records found for patient ID %d".formatted(patientId));
        }
        return medicalRecordMapper.toResponseDTO(page.getContent().getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordListResponseDTO> getRecordsByDoctor(
            Long doctorId, Long hospitalId, Pageable pageable) {
        findDoctorInHospital(doctorId, hospitalId);   // validates the doctor exists
        return medicalRecordRepository
                .findAllByDoctorIdAndHospitalId(doctorId, hospitalId, pageable)
                .map(medicalRecordMapper::toListResponseDTO);
    }

    @SneakyThrows
    @Override
    @Transactional(readOnly = true)
    public Page<MedicalRecordListResponseDTO> getRecordsByHospital(
            Long hospitalId, RecordStatus status,
            LocalDate from, LocalDate to, Pageable pageable) {

        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("'from' date must not be after 'to' date.");
        }

        return medicalRecordRepository
                .findAllByHospitalIdFiltered(hospitalId, status, from, to, pageable)
                .map(medicalRecordMapper::toListResponseDTO);
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public MedicalRecordResponseDTO updateRecord(Long id, Long hospitalId,
                                                 MedicalRecordUpdateRequestDTO request) {
        log.info("Updating medical record ID {} in hospital {}", id, hospitalId);

        MedicalRecord record = findRecord(id, hospitalId);

        if (request.getDiagnosis() != null && !request.getDiagnosis().isBlank()) {
            record.setDiagnosis(request.getDiagnosis().trim());
        }
        if (request.getSymptoms() != null) {
            record.setSymptoms(request.getSymptoms());
        }
        if (request.getTreatmentPlan() != null) {
            record.setTreatmentPlan(request.getTreatmentPlan());
        }
        if (request.getFollowUpNotes() != null) {
            record.setFollowUpNotes(request.getFollowUpNotes());
        }
        if (request.getRecordDate() != null) {
            record.setRecordDate(request.getRecordDate());
        }
        if (request.getStatus() != null) {
            record.setStatus(RecordStatus.valueOf(request.getStatus()));
        }

        MedicalRecord updated = medicalRecordRepository.save(record);
        log.info("Medical record ID {} updated", id);
        return medicalRecordMapper.toResponseDTO(updated);
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @SneakyThrows
    @Override
    @Transactional
    public String deleteRecord(Long id, Long hospitalId) {
        log.info("Deleting medical record ID {} in hospital {}", id, hospitalId);

        MedicalRecord record = findRecord(id, hospitalId);

        // Prevent deletion of records tied to an appointment — use ARCHIVED status instead
        if (record.getAppointment() != null) {
            throw new BadRequestException(
                    "Cannot delete a medical record that is linked to an appointment (appointment ID %d). Archive it instead by setting status to ARCHIVED.".formatted(record.getAppointment().getId()));
        }

        medicalRecordRepository.delete(record);
        log.info("Medical record ID {} deleted", id);
        return "Medical record ID %d has been deleted successfully.".formatted(id);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private MedicalRecord findRecord(Long id, Long hospitalId) {
        if (hospitalId != null && hospitalId > 0) {
            return medicalRecordRepository.findByIdAndHospitalId(id, hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Medical record not found with ID %d for hospital ID %d".formatted(id, hospitalId)));
        }
        return medicalRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medical record not found with ID %d".formatted(id)));
    }

    private Patient findPatientInHospital(Long patientId, Long hospitalId) {
        return patientRepository.findByIdAndHospitalId(patientId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID %s in hospital ID %s}".formatted(patientId,hospitalId)));
    }

    private void verifyPatientInHospital(Long patientId, Long hospitalId) {
        if (!patientRepository.existsByIdAndHospitalId(patientId, hospitalId)) {
            throw new ResourceNotFoundException(
                    "Patient not found with ID %d in hospital ID %d".formatted(patientId, hospitalId));
        }
    }

    private Doctor findDoctorInHospital(Long doctorId, Long hospitalId) {
        return doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active doctor not found with ID %d in hospital ID %d".formatted(doctorId, hospitalId)));
    }

    private Appointment findAppointmentInHospital(Long appointmentId, Long hospitalId) {
        return appointmentRepository.findByIdAndHospitalId(appointmentId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with ID %d in hospital ID %d".formatted(appointmentId, hospitalId)));
    }
}
