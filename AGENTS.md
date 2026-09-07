# AGENTS.md - Medi-Care Codebase Architecture & Guidelines

This document provides a comprehensive architectural map, component reference, and development guidelines for AI agents
and developers working on the **Medi-Care** project.

---

## 1. Project Overview & Technology Stack

- **Framework**: Spring Boot 3.5.4 (Java 21)
- **Database**: PostgreSQL (Production/Dev via Supabase) / H2 (In-memory testing)
- **Security**: Spring Security 6, JWT (HttpOnly Cookie + Bearer Tokens), Role Hierarchy
- **Asynchronous Messaging**: Apache Kafka (Spring Kafka, SSL/PEM Certificates)
- **Mail Service**: JavaMailSender (`MimeMessage` HTML email templates)
- **API Documentation**: SpringDoc OpenAPI 3.0 / Swagger UI (`/swagger-ui.html`)
- **Build Tool**: Apache Maven (`mvnw`)

---

## 2. Directory & Package Architecture (`src/main/java/com/care/medi`)

```
src/main/java/com/care/medi/
├── beans/ & config/          # Spring Beans & Security Configuration
│   ├── KafkaConfig.java       # Kafka Producer & Consumer SSL Factory config
│   └── SecurityConfiguration.java # Spring Security Filter Chain & Role Hierarchy
├── controller/               # REST Endpoints (API V1)
│   ├── AuthController.java    # Login, Refresh, Logout, Forgot Password, OTP, Reset, Update Password
│   ├── AppointmentController.java # Appointment scheduling & management
│   ├── DoctorController.java   # Doctor management
│   ├── PatientController.java  # Patient profile management
│   ├── HospitalController.java # Hospital infrastructure management
│   ├── DepartmentController.java # Hospital departments
│   ├── InsuranceController.java# Insurance records
│   ├── MedicalRecordController.java # Patient medical history
│   ├── PrescriptionController.java # Doctor prescriptions
│   ├── StaffController.java    # Staff records
│   ├── UserAdminController.java# Admin user management
│   ├── AdminDashboardController.java # Admin analytics & metrics
│   └── HealthController.java   # Service health status
├── dtos/                     # Data Transfer Objects
│   ├── request/              # Input Request Payloads (Validation annotations)
│   ├── response/             # Standardized ApiResponse & Response DTOs
│   └── EmailNotificationEvent.java # Kafka Message Event DTO
├── emails/                   # Mail Service & HTML Email Templates
│   └── EmailService.java     # Async MimeMessage sender & HTML templates
├── entity/                   # JPA Persistence Entities
│   ├── BaseEntity.java       # MappedSuperclass (id, createdAt, updatedAt, isDeleted)
│   ├── Users.java            # User credentials, roles, login status
│   ├── OtpTable.java         # OTP generation & 5-min expiration tracking
│   ├── Patient.java, Doctor.java, Staff.java, Hospital.java, Department.java, Appointment.java
│   └── Insurance.java, MedicalRecord.java, Prescription.java, Address.java
├── exception/                # Exception Handling & Hierarchy
│   ├── GlobalExceptionHandler.java # Centralized @RestControllerAdvice
│   └── Custom Exceptions     # UserNotFoundException, InvalidCredentialsException, InvalidRequestException, etc.
├── repository/               # Spring Data JPA Repositories
│   ├── UsersRepository.java  # User queries (findByEmail, existsByEmail)
│   ├── OtpTableRepository.java # OTP queries (findByEmailAndOtp, deleteByEmail)
│   └── Domain Repositories   # AppointmentRepository, DoctorRepository, PatientRepository, etc.
├── security/                 # JWT Authentication Filters & Token Provider
│   ├── JwtService.java       # Access Token & Refresh Token generation/validation
│   └── JwtAuthenticationFilter.java # OncePerRequestFilter for Cookie/Header JWT processing
├── services/                 # Business Logic Services
│   ├── AuthService.java      # Login, Refresh, Forgot Password, Verify OTP, Reset/Update Password
│   ├── Domain Services       # AppointmentServiceImpl, DoctorServiceImpl, PatientServiceImpl, etc.
│   └── kafka/                # Kafka Subpackage
│       ├── EmailNotificationProducer.java # Sends events to Kafka topic
│       └── EmailNotificationConsumer.java # Listens to Kafka topic & routes via switch-case
└── utils/                    # Common Utilities & Constants
    ├── Constants.java        # Topic names, date-time formatters, error messages
    ├── CertificateUtils.java # SSL PEM Certificate handler
    └── Helpers.java          # Entity name & email resolution helpers
```

---

## 3. Key Feature Workflows

### 3.1 Authentication & Password Management (`/api/v1/auth`)

1. **Login (`POST /login`)**: Authenticates credentials, sets HttpOnly `jwt` cookie, returns `AuthTokens`.
2. **Refresh (`POST /refresh`)**: Issues a new access token using a valid refresh token.
3. **Logout (`POST /logout`)**: Clears the HttpOnly `jwt` cookie.
4. **Forgot Password (`POST /forgot-password`)**:
    - Generates 6-digit OTP code.
    - Deletes prior OTPs for user and saves new `OtpTable` record (5-minute expiry).
    - Publishes `FORGOT_PASSWORD_OTP` event to Kafka via `EmailNotificationProducer`.
5. **Verify OTP (`POST /verify-otp`)**: Checks if OTP exists and is non-expired.
6. **Reset Password (`POST /reset-password`)**:
    - Verifies OTP.
    - Updates user's password with BCrypt encoder.
    - Deletes OTP record.
    - Publishes `PASSWORD_CHANGED` event to Kafka via `EmailNotificationProducer`.
7. **Update Password (`POST /update-password`)**:
    - Authenticated user endpoint (`@PreAuthorize("isAuthenticated()")`).
    - Verifies current password, updates to new password.
    - Publishes `PASSWORD_CHANGED` event to Kafka.

### 3.2 Kafka Notification Event Flow

- **Kafka Topic**: `medicare.email.notification` (`Constants.KAFKA_TOPIC`)
- **Producer**: `EmailNotificationProducer.sendEmailNotification(...)`, `sendOtpNotification(...)`,
  `sendPasswordChangedNotification(...)`.
- **Consumer**: `EmailNotificationConsumer.consume(EmailNotificationEvent)`
    - Uses `switch(eventType)` matching:
        - `"FORGOT_PASSWORD_OTP"` -> `emailService.sendOtpEmail(...)`
        - `"PASSWORD_CHANGED"` -> `emailService.sendPasswordChangedEmail(...)`
        - `default` -> `emailService.sendAppointmentConfirmation(...)`

---

## 4. Coding Conventions & Best Practices

1. **ApiResponse Wrapper**: All controller responses MUST be wrapped in `ResponseEntity<ApiResponse<T>>`.
2. **DTO Validation**: Use Jakarta annotations (`@NotBlank`, `@Email`, `@Size`, `@NotNull`).
3. **Soft Delete**: Entities inherit `BaseEntity` and use `@SQLDelete` / `@SQLRestriction("is_deleted = false")`.
4. **Transactional Annotations**: Use `@Transactional` on state-mutating service methods, and
   `@Transactional(readOnly = true)` for read methods.
5. **Backward Compatibility**: `EmailNotificationEvent` maintains an explicit 6-arg constructor for appointment
   notifications alongside builder and 8-arg constructors.
