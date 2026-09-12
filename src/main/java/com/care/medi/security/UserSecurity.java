package com.care.medi.security;

import com.care.medi.entity.Users;
import com.care.medi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Custom Spring Security expression evaluator bean used in @PreAuthorize annotations.
 * <p>
 * Ensures fine-grained object-level authorization (IDOR protection) so that non-admin
 * authenticated users (Doctor, Patient, Staff) can only access and modify their own details.
 * </p>
 */
@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final InsuranceRepository insuranceRepository;

    private Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof Users user) {
            return user.getId();
        }
        return null;
    }

    /**
     * Checks if the doctor ID belongs to the authenticated user.
     */
    public boolean isSelfDoctor(Long doctorId, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || doctorId == null) {
            return false;
        }
        return doctorRepository.existsByIdAndUserId(doctorId, userId);
    }

    /**
     * Checks if the patient ID belongs to the authenticated user.
     */
    public boolean isSelfPatient(Long patientId, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || patientId == null) {
            return false;
        }
        return patientRepository.existsByIdAndUserId(patientId, userId);
    }

    /**
     * Checks if the staff ID belongs to the authenticated user.
     */
    public boolean isSelfStaff(Long staffId, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || staffId == null) {
            return false;
        }
        return staffRepository.existsByIdAndUserId(staffId, userId);
    }

    /**
     * Checks if the user ID matches the authenticated user.
     */
    public boolean isSelfUser(Long userId, Authentication authentication) {
        Long authUserId = getAuthenticatedUserId(authentication);
        if (authUserId == null || userId == null) {
            return false;
        }
        return authUserId.equals(userId);
    }

    /**
     * Checks if the appointment belongs to the patient or assigned doctor.
     */
    public boolean isAppointmentOwnerOrDoctor(Long appointmentId, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || appointmentId == null) {
            return false;
        }
        return appointmentRepository.isPatientOrDoctorOfAppointment(appointmentId, userId);
    }

    /**
     * Checks if the appointment is assigned to the doctor.
     */
    public boolean isAppointmentDoctor(Long appointmentId, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || appointmentId == null) {
            return false;
        }
        return appointmentRepository.isDoctorOfAppointment(appointmentId, userId);
    }

    /**
     * Checks if the medical record belongs to the patient or doctor.
     */
    public boolean isMedicalRecordOwnerOrDoctor(Long recordId, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || recordId == null) {
            return false;
        }
        return medicalRecordRepository.isPatientOrDoctorOfRecord(recordId, userId);
    }

    /**
     * Checks if the insurance policy belongs to the patient.
     */
    public boolean isInsuranceOwner(String policyNumber, Authentication authentication) {
        Long userId = getAuthenticatedUserId(authentication);
        if (userId == null || policyNumber == null) {
            return false;
        }
        return insuranceRepository.existsByPolicyNumberAndPatientUserId(policyNumber, userId);
    }
}
