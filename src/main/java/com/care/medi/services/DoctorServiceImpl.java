package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.AddressResponseDTO;
import com.care.medi.dtos.response.AppointmentListResponseDTO;
import com.care.medi.dtos.response.DoctorListResponseDTO;
import com.care.medi.dtos.response.DoctorResponseDTO;
import com.care.medi.entity.*;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.repository.UserRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DepartmentRepository departmentRepository;
    private final AddressServiceImpl addressService;
    private final AppointmentServiceImpl appointmentService;

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public DoctorResponseDTO createDoctorInHospital(Long hospitalId, DoctorRequestDTO request) {
        Users user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user != null) {
            throw new DuplicateResourceException(Constants.DUPLICATE_DOCTOR + request.getEmail());
        }
        user = Users.builder()
                .email(request.getEmail())
                .passwordHash("default")
                .role(Role.DOCTOR)
                .isActive(true)
                .build();
        Users save = userRepository.save(user);
        Hospital hospital = null;
        if (hospitalId != null) {
            hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + request.getHospitalId()));
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.DEPARTMENT_NOT_FOUND + request.getDepartmentId()));
        }

        Doctor doctor = Doctor.builder()
                .user(save)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(Gender.valueOf(request.getGender()))
                .phone(request.getPhone())
                .speciality(request.getSpeciality())
                .hospital(hospital)
                .department(department)
                .emergencyContact(request.getEmergencyContact())
                .bloodGroup(BloodGroup.valueOf(request.getBloodType()))
                .build();

        return toResponse(doctorRepository.save(doctor));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public Page<DoctorListResponseDTO> getAllActiveDoctorsByHospital(Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> byHospitalId = doctorRepository.findByHospitalIdAndIsActiveTrue(hospitalId, pageable);
        List<DoctorListResponseDTO> list = byHospitalId.stream()
                .map(this::toDoctorListResponse)
                .toList();
        return new PageImpl<>(
                list,
                pageable,
                byHospitalId.getTotalElements()
        );
    }

    @Override
    public Page<DoctorListResponseDTO> getActiveDoctorsByDepartmentAndHospital(Long departmentId, Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> byDepartmentId = doctorRepository.findByHospitalIdAndDepartmentIdAndIsActiveTrue(hospitalId, departmentId, pageable);
        List<DoctorListResponseDTO> list = byDepartmentId.stream()
                .map(this::toDoctorListResponse)
                .toList();
        return new PageImpl<>(
                list,
                pageable,
                byDepartmentId.getTotalElements()
        );
    }

    @Override
    public Page<DoctorListResponseDTO> getActiveDoctorsBySpecialityAndHospital(String speciality, Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> bySpeciality = doctorRepository.findByHospitalIdAndSpecialityContainingIgnoreCaseAndIsActiveTrue(hospitalId,speciality, pageable);
        List<DoctorListResponseDTO> list1 = bySpeciality.stream()
                .map(this::toDoctorListResponse)
                .toList();
        return new PageImpl<>(
                list1,
                pageable,
                bySpeciality.getTotalElements()
        );
    }

    @Override
    public DoctorResponseDTO getDoctorByIdAndHospital(Long id, Long hospitalId) {
        Doctor doctor = doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(id,hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id));
        List<AddressResponseDTO> addresses = addressService.getAddressesByDoctorId(doctor.getUser().getId());
        return toResponse(doctor,addresses);
    }

    @Override
    public Page<DoctorListResponseDTO> getAllActiveDoctors(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> doctors = doctorRepository.findByIsActiveTrue(pageable);
        List<DoctorListResponseDTO> list = doctors.stream().map(this::toDoctorListResponse).toList();
        return new PageImpl<>(
                list,
                pageable,
                doctors.getTotalElements()
        );
    }


    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByDoctorAndHospitalAndDate(Long id, Long hospitalId, LocalDate date, Integer page, Integer size, String sortBy) {
        boolean b = doctorRepository.existsById(id);
        if (!b) throw new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id);
        return appointmentService.getAppointmentsByDoctorAndDate(id, page, size, sortBy, date);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public DoctorResponseDTO updateDoctorByIdAndHospital(Long id, Long hospitalId, DoctorUpdateRequestDTO request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id));

        if (request.getFirstName() != null) doctor.setFirstName(request.getFirstName());
        if (request.getLastName() != null) doctor.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) doctor.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) doctor.setGender(Gender.valueOf(request.getGender()));
        if (request.getPhone() != null) doctor.setPhone(request.getPhone());
        if (request.getSpeciality() != null) doctor.setSpeciality(request.getSpeciality());
        if (request.getEmergencyContact() != null) doctor.setEmergencyContact(request.getEmergencyContact());
        if (request.getBloodType() != null) doctor.setBloodGroup(BloodGroup.valueOf(request.getBloodType()));

        if (hospitalId != null) {
            Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + request.getHospitalId()));
            doctor.setHospital(hospital);
        }
        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.DEPARTMENT_NOT_FOUND + request.getDepartmentId()));
            doctor.setDepartment(dept);
        }

        return toResponse(doctorRepository.save(doctor));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public void deleteDoctorByIdAndHospital(Long doctorId, Long hospitalId) {
        Doctor byId = doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId,hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + doctorId));
        byId.setActive(false);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    DoctorResponseDTO toResponse(Doctor d) {
        return DoctorResponseDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender().toString())
                .phone(d.getPhone())
                .speciality(d.getSpeciality())
                .hospitalId(d.getHospital() != null ? d.getHospital().getId() : null)
                .hospitalName(d.getHospital() != null ? d.getHospital().getName() : null)
                .departmentId(d.getDepartment() != null ? d.getDepartment().getId() : null)
                .departmentName(d.getDepartment() != null ? d.getDepartment().getName() : null)
                .emergencyContact(d.getEmergencyContact())
                .bloodType(d.getBloodGroup().toString())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }

    DoctorResponseDTO toResponse(Doctor d,List<AddressResponseDTO> addresses) {
        return DoctorResponseDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender().toString())
                .phone(d.getPhone())
                .speciality(d.getSpeciality())
                .hospitalId(d.getHospital() != null ? d.getHospital().getId() : null)
                .hospitalName(d.getHospital() != null ? d.getHospital().getName() : null)
                .departmentId(d.getDepartment() != null ? d.getDepartment().getId() : null)
                .departmentName(d.getDepartment() != null ? d.getDepartment().getName() : null)
                .emergencyContact(d.getEmergencyContact())
                .bloodType(d.getBloodGroup().toString())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .addresses(addresses)
                .build();
    }

    private DoctorListResponseDTO toDoctorListResponse(Doctor d) {
        return DoctorListResponseDTO.builder()
                .id(d.getId())
                .firstName(d.getFirstName())
                .lastName(d.getLastName())
                .dateOfBirth(d.getDateOfBirth())
                .gender(d.getGender().toString())
                .phone(d.getPhone())
                .speciality(d.getSpeciality())
                .hospitalId(d.getHospital() != null ? d.getHospital().getId() : null)
                .hospitalName(d.getHospital() != null ? d.getHospital().getName() : null)
                .departmentId(d.getDepartment() != null ? d.getDepartment().getId() : null)
                .departmentName(d.getDepartment() != null ? d.getDepartment().getName() : null)
                .emergencyContact(d.getEmergencyContact())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
