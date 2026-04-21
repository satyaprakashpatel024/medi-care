package com.care.medi.utils;

import com.care.medi.dtos.response.MedicalRecordListResponseDTO;
import com.care.medi.dtos.response.MedicalRecordResponseDTO;
import com.care.medi.entity.MedicalRecord;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordMapper {

    private static final int DIAGNOSIS_PREVIEW_LENGTH = 120;
    private static final String NAME_FORMAT = "%s %s";

    /**
     * Full detail mapping — used for single-record GET, POST, PUT responses.
     */
    public MedicalRecordResponseDTO toResponseDTO(MedicalRecord record) {
        var patient = record.getPatient();
        var doctor  = record.getDoctor();
        var appt    = record.getAppointment();

        return MedicalRecordResponseDTO.builder()
                .id(record.getId())

                // Patient
                .patientId(patient.getId())
                .patientName(NAME_FORMAT.formatted(patient.getFirstName(), patient.getLastName()))
                .patientBloodGroup(patient.getBloodGroup() != null
                        ? patient.getBloodGroup().name() : null)
                .patientDateOfBirth(patient.getDateOfBirth())

                // Doctor
                .doctorId(doctor.getId())
                .doctorName(NAME_FORMAT.formatted(doctor.getFirstName(), doctor.getLastName()))
                .doctorSpeciality(doctor.getSpeciality())
                .departmentId(doctor.getDepartment() != null
                        ? doctor.getDepartment().getId() : null)
                .departmentName(doctor.getDepartment() != null
                        ? doctor.getDepartment().getName() : null)

                // Appointment (nullable)
                .appointmentId(appt != null ? appt.getId() : null)
                .appointmentDate(appt != null && appt.getAppointmentDate() != null
                        ? appt.getAppointmentDate().toString() : null)

                // Hospital
                .hospitalId(record.getHospitalId())
                .hospitalName(doctor.getHospital() != null
                        ? doctor.getHospital().getName() : null)

                // Clinical
                .diagnosis(record.getDiagnosis())
                .symptoms(record.getSymptoms())
                .treatmentPlan(record.getTreatmentPlan())
                .followUpNotes(record.getFollowUpNotes())
                .recordDate(record.getRecordDate())
                .status(record.getStatus().name())

                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    /**
     * Lean list projection — used inside paginated list responses.
     * Diagnosis is truncated to keep list payloads readable.
     */
    public MedicalRecordListResponseDTO toListResponseDTO(MedicalRecord record) {
        var patient = record.getPatient();
        var doctor  = record.getDoctor();
        var appt    = record.getAppointment();

        String diagnosisPreview = record.getDiagnosis() != null &&
                record.getDiagnosis().length() > DIAGNOSIS_PREVIEW_LENGTH
                ? "%s…".formatted(record.getDiagnosis().substring(0, DIAGNOSIS_PREVIEW_LENGTH))
                : record.getDiagnosis();

        return MedicalRecordListResponseDTO.builder()
                .id(record.getId())
                .patientId(patient.getId())
                .patientName(NAME_FORMAT.formatted(patient.getFirstName(), patient.getLastName()))
                .doctorId(doctor.getId())
                .doctorName(NAME_FORMAT.formatted(doctor.getFirstName(), doctor.getLastName()))
                .doctorSpeciality(doctor.getSpeciality())
                .appointmentId(appt != null ? appt.getId() : null)
                .diagnosis(diagnosisPreview)
                .recordDate(record.getRecordDate())
                .status(record.getStatus().name())
                .createdAt(record.getCreatedAt())
                .build();
    }
}