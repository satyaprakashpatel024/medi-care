# 🏥 Medi-Care - Healthcare & Hospital Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://jdk.java.net/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Event--Driven-red.svg?style=flat-square&logo=apachekafka)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg?style=flat-square&logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue.svg?style=flat-square&logo=docker)](https://www.docker.com/)
[![Swagger](https://img.shields.io/badge/OpenAPI-3.0-green.svg?style=flat-square&logo=swagger)](http://localhost:8080/swagger-ui.html)

**Medi-Care** is an enterprise-grade RESTful Healthcare and Hospital Management System built with **Spring Boot 3.5.4** and **Java 21**. It delivers comprehensive management of hospital infrastructure, departments, medical staff, doctor-patient relationships, appointment scheduling, digital prescriptions, medical history records, and insurance coverage.

---

## 📑 Table of Contents

- [Core Features](#-core-features)
- [Architecture & Tech Stack](#-architecture--tech-stack)
- [Project Directory Structure](#-project-directory-structure)
- [Key Feature Workflows](#-key-feature-workflows)
  - [Authentication & JWT Security](#1-authentication--jwt-security)
  - [Kafka Asynchronous Email Pipeline](#2-kafka-asynchronous-email-pipeline)
  - [Data Integrity & Soft Deletes](#3-data-integrity--soft-deletes)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Environment Profiles & Configuration](#environment-profiles--configuration)
  - [Running with Maven](#running-locally-via-maven)
  - [Running with Docker Compose](#running-with-docker-compose)
- [API Reference & Endpoints](#-api-reference--endpoints)
- [API Documentation & Swagger UI](#-api-documentation--swagger-ui)
- [Testing & Quality Assurance](#-testing--quality-assurance)

---

## ✨ Core Features

- **🔐 Dual Authentication & Authorization**: Support for HttpOnly cookie (`jwt`) and `Bearer` token authentication with fine-grained Role-Based Access Control (`ROLE_ADMIN`, `ROLE_DOCTOR`, `ROLE_PATIENT`, `ROLE_STAFF`).
- **📧 Event-Driven Asynchronous Notifications**: Apache Kafka producer-consumer pipeline delivering HTML email notifications for OTP verification, password resets, and appointment updates via `JavaMailSender`.
- **🏥 Hospital & Department Management**: Infrastructure management supporting multi-hospital operations and department mappings.
- **👨‍⚕️ Doctor & Patient Profiles**: Specialized entity tracking for medical staff, doctor specializations, shift schedules, and patient history.
- **📅 Appointment Lifecycle Management**: Complete workflow for scheduling, updating, canceling, and completing appointments with doctor availability validation.
- **📋 Digital Prescriptions & Medical Records**: Structured tracking of patient diagnoses, treatments, dosage instructions, and history records.
- **💳 Insurance Record Tracking**: Association of insurance policies and provider details with patient accounts.
- **⚡ Redis Caching & Actuator Monitoring**: Redis integration for high-performance data caching alongside Spring Boot Actuator with Prometheus metrics for health monitoring.
- **🐳 Minimal Containerized Deployment**: Multi-stage Docker build utilizing `jlink` to produce an ultra-lightweight ~45MB custom JRE running on Alpine Linux.

---

## 🛠 Architecture & Tech Stack

| Domain / Layer | Technology / Library | Description |
| :--- | :--- | :--- |
| **Language & Runtime** | Java 21 LTS | Modern Java features (record patterns, virtual threads readiness) |
| **Core Framework** | Spring Boot 3.5.4 | Framework base with Spring MVC, Data JPA, Security, Actuator |
| **Database** | PostgreSQL 16 / H2 | PostgreSQL for dev/prod, H2 for fast unit & integration tests |
| **Database Migrations** | Flyway | Automated SQL schema migrations (`org.flywaydb`) |
| **Security** | Spring Security 6 + JJWT | JWT authentication filter, BCrypt password encoding, Role hierarchy |
| **Async Messaging** | Apache Kafka | Event producer/consumer with SSL certificate handling |
| **Caching** | Redis (Spring Data Redis) | Distributed caching and session management |
| **Mail Delivery** | Spring Starter Mail | Asynchronous `MimeMessage` HTML email processing |
| **API Docs** | SpringDoc OpenAPI 3.0 | Interactive Swagger UI (`springdoc-openapi-starter-webmvc-ui`) |
| **Build & Test** | Maven, JUnit 5, Mockito, JaCoCo | Automated build pipeline with code coverage metrics |

---

## 📁 Project Directory Structure

```
src/main/java/com/care/medi/
├── MediCareApplication.java      # Application main entry point
├── beans/ & config/              # Spring configuration & beans
│   ├── KafkaConfig.java          # Kafka Producer/Consumer SSL Factory setup
│   └── SecurityConfiguration.java# Spring Security filter chain & role hierarchy
├── controller/                   # REST API V1 Controllers
│   ├── AuthController.java       # Login, Refresh, Logout, Password Management, OTP
│   ├── AppointmentController.java# Appointment scheduling & management
│   ├── DoctorController.java      # Doctor management & schedules
│   ├── PatientController.java     # Patient profile management
│   ├── HospitalController.java    # Hospital facility endpoints
│   ├── DepartmentController.java  # Department management
│   ├── AdminDashboardController.java # System statistics & analytics
│   ├── MedicalRecordController.java  # Medical records & history
│   ├── PrescriptionController.java   # Prescription creation & details
│   ├── InsuranceController.java   # Patient insurance information
│   ├── StaffController.java       # Non-doctor staff management
│   ├── UserAdminController.java   # Admin user account control
│   └── HealthController.java      # Liveness & health status endpoint
├── dtos/                         # Data Transfer Objects
│   ├── request/                  # Request payloads (Jakarta @Valid annotations)
│   ├── response/                 # Standardized ApiResponse<T> wrapper & DTOs
│   └── EmailNotificationEvent.java # Kafka event message model
├── emails/                       # Async MimeMessage sender & HTML templates
│   └── EmailService.java        
├── entity/                       # JPA Persistence Entities
│   ├── BaseEntity.java           # MappedSuperclass (id, createdAt, updatedAt, isDeleted)
│   ├── Users.java, OtpTable.java # User identity & OTP tracking
│   ├── Patient.java, Doctor.java # Domain entities
│   ├── Hospital.java, Department.java
│   └── Appointment.java, MedicalRecord.java, Prescription.java, Insurance.java
├── exception/                    # Global Exception Handling
│   └── GlobalExceptionHandler.java # Centralized @RestControllerAdvice
├── repository/                   # Spring Data JPA Repositories
├── security/                     # Security Filters & JWT Provider
│   ├── JwtService.java           # Token creation, parser & validation
│   └── JwtAuthenticationFilter.java # HttpOnly Cookie & Bearer header filter
├── services/                     # Business Logic Layer & Services
│   └── kafka/                    # Kafka Producer & Consumer listeners
└── utils/                        # System constants, Certificate & Helper utilities
```

---

## 🔑 Key Feature Workflows

### 1. Authentication & JWT Security

- **Login (`POST /api/v1/auth/login`)**: Validates credentials and sets an `HttpOnly` secure `jwt` cookie while returning an `AuthTokens` access token payload.
- **Refresh Token (`POST /api/v1/auth/refresh`)**: Issues a fresh access token using a valid refresh token.
- **Logout (`POST /api/v1/auth/logout`)**: Invalidates the current session and clears the HttpOnly cookie.
- **Forgot Password Workflow**:
  1. `POST /api/v1/auth/forgot-password`: Generates a 6-digit numeric OTP code (5-minute expiration) and publishes a `FORGOT_PASSWORD_OTP` event to Kafka.
  2. `POST /api/v1/auth/verify-otp`: Validates the OTP.
  3. `POST /api/v1/auth/reset-password`: Updates the user password with BCrypt, deletes the used OTP, and dispatches a `PASSWORD_CHANGED` Kafka notification event.

### 2. Kafka Asynchronous Email Pipeline

- **Kafka Topic**: `medicare.email.notification`
- **Producer**: `EmailNotificationProducer` publishes typed notification payloads (`EmailNotificationEvent`).
- **Consumer**: `EmailNotificationConsumer` listens on the topic and routes events via a switch-case router to `EmailService`:
  - `"FORGOT_PASSWORD_OTP"` → Triggers HTML OTP Email Template.
  - `"PASSWORD_CHANGED"` → Triggers Password Change Confirmation Template.
  - `Default` → Triggers Appointment Confirmation Template.

### 3. Data Integrity & Soft Deletes

All domain entities extend `BaseEntity`, implementing unified audit fields (`createdAt`, `updatedAt`, `isDeleted`). Deletions execute soft updates via Hibernate annotations `@SQLDelete` and `@SQLRestriction("is_deleted = false")`, preserving historical database records for compliance.

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 21** or higher
- **Maven 3.9+** (or use included `./mvnw` wrapper)
- **PostgreSQL 16** (or Docker container)
- **Redis 7** (optional for dev, required for prod cache)
- **Docker & Docker Compose** (optional for containerized deployment)

### Environment Profiles & Configuration

Configuration settings are organized in `src/main/resources/`:
- `application.properties`: Core application defaults & profile selector.
- `application-dev.properties`: Active development profile (Default).
- `application-local.properties`: Environment overrides for local development.
- `application-prod.properties`: Production setup reading environment variables.

#### Key Environment Variables (Production / Docker):

```env
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/medicare
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgrespassword
SPRING_REDIS_URL=redis://redis:6379
KAFKA_BOOTSTRAP_SERVERS=medicare-kafka-cloud:20940
KAFKA_USERNAME=your_kafka_username
KAFKA_PASSWORD=your_kafka_password
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@gmail.com
SPRING_MAIL_PASSWORD=your_smtp_app_password
JWT_SECRET=your_base64_encoded_256bit_secret_key
```

### Running Locally via Maven

1. Clone the repository:
   ```bash
   git clone https://github.com/satyaprakashpatel024/medi-care.git
   cd medi-care
   ```

2. Build and run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. The application will start on port `8080`.

### Running with Docker Compose

Spin up PostgreSQL, Redis, and the Medi-Care Spring Boot application in unified Docker containers:

```bash
docker compose up --build -d
```

To stop all services:
```bash
docker compose down
```

---

## 🌐 API Reference & Endpoints

All responses follow the unified `ApiResponse<T>` JSON envelope:
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... },
  "timestamp": "2026-09-14T14:30:00"
}
```

### Key API Endpoint Summary

| Category | HTTP Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- | :---: |
| **Auth** | `POST` | `/api/v1/auth/login` | Authenticate user & receive JWT cookie / token | ❌ |
| **Auth** | `POST` | `/api/v1/auth/refresh` | Refresh expired access token | ❌ |
| **Auth** | `POST` | `/api/v1/auth/logout` | Clear user session cookie | ❌ |
| **Auth** | `POST` | `/api/v1/auth/forgot-password` | Request password reset OTP email | ❌ |
| **Auth** | `POST` | `/api/v1/auth/verify-otp` | Verify 6-digit OTP code | ❌ |
| **Auth** | `POST` | `/api/v1/auth/reset-password` | Reset password using valid OTP | ❌ |
| **Auth** | `POST` | `/api/v1/auth/update-password` | Update current password | ✅ |
| **Appointments**| `POST` | `/api/v1/appointments` | Book new patient appointment | ✅ |
| **Appointments**| `GET` | `/api/v1/appointments/{id}` | Get appointment details | ✅ |
| **Appointments**| `PUT` | `/api/v1/appointments/{id}/status`| Update appointment status | ✅ |
| **Patients** | `GET` | `/api/v1/patients` | Fetch paginated patients | ✅ (`ADMIN`, `DOCTOR`) |
| **Patients** | `POST` | `/api/v1/patients` | Register new patient profile | ✅ |
| **Doctors** | `GET` | `/api/v1/doctors` | List doctors by department/specialization | ❌ |
| **Doctors** | `POST` | `/api/v1/doctors` | Register doctor profile | ✅ (`ADMIN`) |
| **Hospitals** | `GET` | `/api/v1/hospitals` | List all active hospital locations | ❌ |
| **Departments**| `GET` | `/api/v1/departments` | List hospital departments | ❌ |
| **Prescriptions**|`POST` | `/api/v1/prescriptions` | Issue new medical prescription | ✅ (`DOCTOR`) |
| **Medical Records**|`POST`| `/api/v1/medical-records` | Add patient medical history record | ✅ (`DOCTOR`) |
| **Admin** | `GET` | `/api/v1/admin/dashboard` | Retrieve system analytics & stats | ✅ (`ADMIN`) |
| **Health** | `GET` | `/api/v1/health` | Check service health status | ❌ |

---

## 📖 API Documentation & Swagger UI

Interactive API testing and documentation are automatically generated via OpenAPI 3.0 when the application is running:

- **Swagger Interactive UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🧪 Testing & Quality Assurance

### Run Unit & Integration Tests

Execute the complete test suite with in-memory H2 database configuration:

```bash
./mvnw clean test
```

### Generate Code Coverage Reports

Generate JaCoCo test coverage reports (`target/site/jacoco/index.html`):

```bash
./mvnw jacoco:report
```

For detailed unit test documentation, refer to [UNIT_TESTS_README.md](file:///Users/satyapatel/IdeaProjects/medi-care/UNIT_TESTS_README.md).

---

## 📄 License

This project is licensed under the MIT License - see the project repository for details.
