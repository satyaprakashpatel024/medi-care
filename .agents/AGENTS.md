# AGENTS.md - Medi-Care Workspace Customization & Rules

This file is automatically scanned by AI agents to guide coding patterns, security rules, and architectural standards in
the **Medi-Care** repository.

---

## 1. Quick System Map

- **Auth & Password Flow
  **: [AuthService.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/services/AuthService.java) & [AuthController.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/controller/AuthController.java)
- **Kafka Notifications
  **: [EmailNotificationProducer.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/services/kafka/EmailNotificationProducer.java) & [EmailNotificationConsumer.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/services/kafka/EmailNotificationConsumer.java)
- **HTML Email Sender
  **: [EmailService.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/emails/EmailService.java)
- **Security & JWT
  **: [SecurityConfiguration.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/config/SecurityConfiguration.java) & [JwtService.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/security/JwtService.java)
- **Global Error Handling
  **: [GlobalExceptionHandler.java](file:///Users/satyapatel/IdeaProjects/medi-care/src/main/java/com/care/medi/exception/GlobalExceptionHandler.java)

---

## 2. Core Architectural Rules

1. **ApiResponse Pattern**: Return `ApiResponse.success(message, data)` or
   `ApiResponse.error(message, errorCode, status)`.
2. **DTOs Layering**: Place input request DTOs in `com.care.medi.dtos.request` and response DTOs in
   `com.care.medi.dtos.response`.
3. **Kafka Events**: When extending `EmailNotificationEvent`, preserve constructors to avoid breaking callers in
   `AppointmentServiceImpl`.
4. **Exception Handling**: Throw specialized exceptions (`InvalidRequestException`, `UserNotFoundException`,
   `InvalidCredentialsException`, etc.) and let `GlobalExceptionHandler` format the response.
5. **Database Entities**: Extend `BaseEntity` for all JPA entities to include `id`, `createdAt`, `updatedAt`, and
   `isDeleted`.
