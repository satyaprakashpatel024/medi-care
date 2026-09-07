# 🚀 Quick Start & Project Guide - Medi-Care

Welcome to **Medi-Care**! This document provides instructions on how to start the application, where to inspect key
entry points, and how the core modules work.

---

## 🏁 Where to Start

### 1. Key Entry Points

- **Application Main Class
  **: [MediCareApplication.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/MediCareApplication.java)
- **Security Configuration
  **: [SecurityConfiguration.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/config/SecurityConfiguration.java)
- **Authentication & Password Endpoints
  **: [AuthController.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/controller/AuthController.java)
- **Auth Business Logic
  **: [AuthService.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/services/AuthService.java)
- **Kafka Notifications**:
    -
    Producer: [EmailNotificationProducer.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/services/kafka/EmailNotificationProducer.java)
    -
    Consumer: [EmailNotificationConsumer.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/services/kafka/EmailNotificationConsumer.java)
- **HTML Email Templates
  **: [EmailService.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/emails/EmailService.java)
- **Architecture Overview File**: [AGENTS.md](file:///Users/satyapatel/IdeaProjects/medi-care/AGENTS.md)

---

## 🛠️ How to Run the Application

### 1. Prerequisites

- **Java 21**
- **Maven 3.x** or `./mvnw` wrapper

### 2. Configuration Profiles

The application uses Spring profiles defined in `src/main/resources/`:

- `application-dev.properties` (Default active profile)
- `application-local.properties`
- `application-prod.properties`

### 3. Run Commands

#### Run locally via Maven:

```bash
./mvnw spring-boot:run
```

#### Run tests:

```bash
./mvnw test
```

---

## 📚 API Documentation & Interactive UI

Once the application starts (default port `8080`):

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: `http://localhost:8080/v3/api-docs`

---

## 🔑 Core Authentication Endpoints Summary

| HTTP Method | Endpoint                       | Description                              | Auth Required |
|-------------|--------------------------------|------------------------------------------|---------------|
| `POST`      | `/api/v1/auth/login`           | Login user, issue JWT cookie             | No            |
| `POST`      | `/api/v1/auth/refresh`         | Refresh JWT access token                 | No            |
| `POST`      | `/api/v1/auth/logout`          | Logout user, clear JWT cookie            | No            |
| `POST`      | `/api/v1/auth/forgot-password` | Generate OTP & publish Kafka email event | No            |
| `POST`      | `/api/v1/auth/verify-otp`      | Verify account email OTP                 | No            |
| `POST`      | `/api/v1/auth/reset-password`  | Reset password using verified OTP        | No            |
| `POST`      | `/api/v1/auth/update-password` | Update password for logged-in user       | Yes           |

---

## 💡 AI / Agent Reference

For a complete module map, entity list, exception hierarchy, and design rules,
consult [AGENTS.md](file:///Users/satyapatel/IdeaProjects/medi-care/AGENTS.md).
