package com.care.medi.config;

import com.care.medi.emails.EmailService;
import com.care.medi.repository.*;
import com.care.medi.services.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Test configuration to mock all repository and service dependencies
 * needed when running WebMvcTest controller tests.
 */
@TestConfiguration
public class WebMvcTestConfig {

    // Mock all repositories
    @MockitoBean
    private UsersRepository usersRepository;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private DoctorRepository doctorRepository;

    @MockitoBean
    private HospitalRepository hospitalRepository;

    @MockitoBean
    private DepartmentRepository departmentRepository;

    @MockitoBean
    private AppointmentRepository appointmentRepository;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;

    @MockitoBean
    private PrescriptionRepository prescriptionRepository;

    @MockitoBean
    private StaffRepository staffRepository;

    @MockitoBean
    private InsuranceRepository insuranceRepository;

    @MockitoBean
    private OtpTableRepository otpTableRepository;

    @MockitoBean
    private AddressRepository addressRepository;

    // Mock all services (except the ones being tested, which will be individually mocked in specific tests)
    @MockitoBean
    private AppointmentService appointmentService;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private DoctorService doctorService;

    @MockitoBean
    private HospitalService hospitalService;

    @MockitoBean
    private DepartmentService departmentService;

    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private StaffService staffService;

    @MockitoBean
    private InsuranceService insuranceService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private AdminAnalyticsService adminAnalyticsService;

    @MockitoBean
    private HospitalDepartmentService hospitalDepartmentService;

    @MockitoBean
    private UserAdminService userAdminService;
}

