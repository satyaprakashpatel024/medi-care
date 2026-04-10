package com.care.medi.services;

import com.care.medi.dtos.request.DoctorRequestDTO;
import com.care.medi.dtos.request.DoctorUpdateRequestDTO;
import com.care.medi.dtos.response.*;
import com.care.medi.entity.*;
import com.care.medi.exception.DuplicateResourceException;
import com.care.medi.exception.ResourceNotFoundException;
import com.care.medi.repository.DepartmentRepository;
import com.care.medi.repository.DoctorRepository;
import com.care.medi.repository.HospitalRepository;
import com.care.medi.repository.UserRepository;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.*;
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
    private final AddressServiceImpl addressService;
    private final AppointmentService appointmentService;

    // ── Create ────────────────────────────────────────────────────────────────

    @SneakyThrows
    @Override
    @Transactional
    public DoctorResponseDTO createDoctor(DoctorRequestDTO request) {
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
        if (request.getHospitalId() != null) {
            hospital = hospitalRepository.findById(request.getHospitalId())
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
    public Page<DoctorListResponseDTO> getDoctorsByHospital(Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> byHospitalId = doctorRepository.findByHospitalId(hospitalId, pageable);
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
    public Page<DoctorListResponseDTO> getDoctorsByDepartmentAndHospital(Long departmentId, Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> byDepartmentId = doctorRepository.findByDepartmentIdAndHospitalId(departmentId, hospitalId, pageable);
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
    public Page<DoctorListResponseDTO> getDoctorsBySpeciality(String speciality, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> bySpeciality = doctorRepository.findBySpecialityContainingIgnoreCase(speciality, pageable);
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
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id));
        return toResponse(doctor);
    }

    @Override
    public Page<DoctorListResponseDTO> getAllDoctors(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> doctors = doctorRepository.findAll(pageable);
        List<DoctorListResponseDTO> list = doctors.stream().map(this::toDoctorListResponse).toList();
        return new PageImpl<>(
                list,
                pageable,
                doctors.getTotalElements()
        );
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorUpdateRequestDTO request) {
        Doctor doctor = doctorRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id));

        if (request.getFirstName() != null) doctor.setFirstName(request.getFirstName());
        if (request.getLastName() != null) doctor.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) doctor.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) doctor.setGender(Gender.valueOf(request.getGender()));
        if (request.getPhone() != null) doctor.setPhone(request.getPhone());
        if (request.getSpeciality() != null) doctor.setSpeciality(request.getSpeciality());
        if (request.getEmergencyContact() != null) doctor.setEmergencyContact(request.getEmergencyContact());
        if (request.getBloodType() != null) doctor.setBloodGroup(BloodGroup.valueOf(request.getBloodType()));

        if (request.getHospitalId() != null) {
            Hospital hospital = hospitalRepository.findById(request.getHospitalId())
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

    @Override
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id);
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
                .addresses(toAddressResponse(d.getAddresses()))
                .build();
    }

    private List<AddressResponseDTO> toAddressResponse(List<Address> a) {
        return a.stream().map(addressService::toAddressResponse)
                .toList();
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

    @Override
    public Page<AppointmentListResponseDTO> getAppointmentsByDoctor(Long id, Integer page, Integer size, String sortBy) {
        Page<AppointmentListResponseDTO> appointmentsByDoctor = appointmentService.getAppointmentsByDoctor(id, page, size, sortBy);
        return appointmentsByDoctor;
    }
}
