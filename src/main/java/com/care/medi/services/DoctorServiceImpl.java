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

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(Constants.DUPLICATE_DOCTOR + request.getEmail());
        }
        if (!hospitalRepository.existsById(hospitalId)) {
            throw new ResourceNotFoundException(Constants.HOSPITAL_NOT_FOUND + request.getHospitalId());
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(Constants.DEPARTMENT_NOT_FOUND + request.getDepartmentId()));
        }

        Users save = userRepository.save(Users.toEntity(request.getEmail(), "default", Role.DOCTOR));
        Doctor doctor = Doctor.toEntity(request,save.getId(),department,hospitalId);

        return DoctorResponseDTO.toResponse(doctorRepository.save(doctor));
    }

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public Page<DoctorListResponseDTO> getAllActiveDoctorsByHospital(Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> byHospitalId = doctorRepository.findByHospitalIdAndIsActiveTrue(hospitalId, pageable);
        return byHospitalId.map(DoctorListResponseDTO::toDoctorListResponse);
    }

    @Override
    public Page<DoctorListResponseDTO> getActiveDoctorsByDepartmentAndHospital(Long departmentId, Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> byDepartmentId = doctorRepository.findByHospitalIdAndDepartmentIdAndIsActiveTrue(hospitalId, departmentId, pageable);
        return byDepartmentId.map(DoctorListResponseDTO::toDoctorListResponse);
    }

    @Override
    public Page<DoctorListResponseDTO> getActiveDoctorsBySpecialityAndHospital(String speciality, Long hospitalId, int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> bySpeciality = doctorRepository.findByHospitalIdAndSpecialityContainingIgnoreCaseAndIsActiveTrue(hospitalId, speciality, pageable);
        return bySpeciality.map(DoctorListResponseDTO::toDoctorListResponse);
    }

    @Override
    public DoctorResponseDTO getDoctorByIdAndHospital(Long id, Long hospitalId) {
        Doctor doctor = doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(id, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + id));
        List<AddressResponseDTO> addresses = addressService.getAddressesByDoctorId(doctor.getUserId());
        return DoctorResponseDTO.toResponse(doctor, addresses);
    }

    @Override
    public Page<DoctorListResponseDTO> getAllActiveDoctors(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<Doctor> doctors = doctorRepository.findByIsActiveTrue(pageable);
        return  doctors.map(DoctorListResponseDTO::toDoctorListResponse);
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

        return DoctorResponseDTO.toResponse(doctorRepository.save(doctor));
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    @Override
    public void deleteDoctorByIdAndHospital(Long doctorId, Long hospitalId) {
        Doctor byId = doctorRepository.findByIdAndHospitalIdAndIsActiveTrue(doctorId, hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.DOCTOR_NOT_FOUND + doctorId));
        byId.setActive(false);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

}
