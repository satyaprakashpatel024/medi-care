package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.emails.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailNotificationConsumer emailNotificationConsumer;

    @Test
    @DisplayName("Should consume FORGOT_PASSWORD_OTP event and send OTP email")
    void testConsumeForgotPasswordOtp() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("user@example.com")
                .otp("654321")
                .eventType("FORGOT_PASSWORD_OTP")
                .build();

        emailNotificationConsumer.consume(event);

        verify(emailService).sendOtpEmail("user@example.com", "654321");
    }

    @Test
    @DisplayName("Should consume PASSWORD_CHANGED event and send password changed email")
    void testConsumePasswordChanged() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("user@example.com")
                .eventType("PASSWORD_CHANGED")
                .build();

        emailNotificationConsumer.consume(event);

        verify(emailService).sendPasswordChangedEmail("user@example.com");
    }

    @Test
    @DisplayName("Should consume appointment confirmation event by default")
    void testConsumeAppointmentConfirmationDefault() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("patient@example.com")
                .patientName("John Doe")
                .doctorName("Dr. Smith")
                .date("2026-09-10")
                .time("10:00 AM")
                .appointmentId(101L)
                .eventType("APPOINTMENT_CONFIRMATION")
                .build();

        emailNotificationConsumer.consume(event);

        verify(emailService).sendAppointmentConfirmation(
                "patient@example.com",
                "John Doe",
                "Dr. Smith",
                "2026-09-10",
                "10:00 AM",
                101L
        );
    }

    @Test
    @DisplayName("Should fallback to FORGOT_PASSWORD_OTP when eventType is null but OTP is present")
    void testConsumeFallbackOtpWhenEventTypeNull() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("user@example.com")
                .otp("112233")
                .eventType(null)
                .build();

        emailNotificationConsumer.consume(event);

        verify(emailService).sendOtpEmail("user@example.com", "112233");
    }
}
