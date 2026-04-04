package com.care.medi.services;

import com.care.medi.dtos.response.*;
import com.care.medi.dtos.request.*;
import com.care.medi.entity.*;
import com.care.medi.exception.BusinessException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;

    // ── Create ────────────────────────────────────────────────────────────────

    @SneakyThrows
    @Override
    @Transactional
    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with this id : %d", request.getUserId())));;

        if (!user.getRole().equals("DOCTOR")) {
            throw new BusinessException("User role must be DOCTOR");
        }
        if (doctorRepository.existsByUserId(request.getUserId())) {
            throw new BusinessException("Doctor profile already exists for this user");
        }

        Hospital hospital = null;
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital not found with this id : %d", request.getHospitalId())));
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Department not found with this id : %d",request.getDepartmentId())));
        }

        Doctor doctor = Doctor.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .speciality(request.getSpeciality())
                .hospital(hospital)
                .department(department)
                .emergencyContact(request.getEmergencyContact())
                .bloodType(request.getBloodType())
                .build();

        return toResponse(doctorRepository.save(doctor));
    }

    @Override
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Doctor not found with this id : %d", id)));
        return toResponse(doctor);
    }

    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public List<DoctorResponseDTO> getDoctorsByHospital(Long hospitalId) {
        return doctorRepository.findByHospitalIdWithDetails(hospitalId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<DoctorResponseDTO> getDoctorsByDepartment(Long departmentId) {
        return doctorRepository.findByDepartmentIdWithDetails(departmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<DoctorResponseDTO> getDoctorsBySpeciality(String speciality) {
        return doctorRepository.findBySpecialityIgnoreCaseWithDetails(speciality).stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequestDTO request) {
        Doctor doctor = doctorRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Doctor not found with this id : %d", id)));

        if (request.getFirstName() != null)       doctor.setFirstName(request.getFirstName());
        if (request.getLastName() != null)         doctor.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null)      doctor.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)           doctor.setGender(request.getGender());
        if (request.getPhone() != null)            doctor.setPhone(request.getPhone());
        if (request.getSpeciality() != null)       doctor.setSpeciality(request.getSpeciality());
        if (request.getEmergencyContact() != null) doctor.setEmergencyContact(request.getEmergencyContact());
        if (request.getBloodType() != null)        doctor.setBloodType(request.getBloodType());

        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Hospital not found with this id : %d", request.getHospitalId())));
            doctor.setHospital(hospital);
        }
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("Department not found with this id : %d", request.getDepartmentId())));
            doctor.setDepartment(dept);
        }

        return toResponse(doctorRepository.save(doctor));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Override
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException(String.format("Doctor not found with this id : %d", id));
        }
        doctorRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    DoctorResponseDTO toResponse(Doctor d) {
        return DoctorResponseDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender())
                .phone(d.getPhone())
                .speciality(d.getSpeciality())
                .hospitalId(d.getHospital() != null ? d.getHospital().getId() : null)
                .hospitalName(d.getHospital() != null ? d.getHospital().getName() : null)
                .departmentId(d.getDepartment() != null ? d.getDepartment().getId() : null)
                .departmentName(d.getDepartment() != null ? d.getDepartment().getName() : null)
                .emergencyContact(d.getEmergencyContact())
                .bloodType(d.getBloodType())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
