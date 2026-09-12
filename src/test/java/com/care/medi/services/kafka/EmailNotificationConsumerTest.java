package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.dtos.OtpNotificationEvent;
import com.care.medi.dtos.PasswordChangedNotificationEvent;
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
    @DisplayName("Should consume OTP notification event and send OTP email")
    void testConsumeForgotPasswordOtp() {
        OtpNotificationEvent event = OtpNotificationEvent.builder()
                .toEmail("user@example.com")
                .otp("654321")
                .build();

        emailNotificationConsumer.consumeOtpNotification(event);

        verify(emailService).sendOtpEmail("user@example.com", "654321");
    }

    @Test
    @DisplayName("Should consume PASSWORD_CHANGED notification event and send email")
    void testConsumePasswordChanged() {
        PasswordChangedNotificationEvent event = PasswordChangedNotificationEvent.builder()
                .toEmail("user@example.com")
                .build();

        emailNotificationConsumer.consumePasswordChangedNotification(event);

        verify(emailService).sendPasswordChangedEmail("user@example.com");
    }

    @Test
    @DisplayName("Should consume appointment notification event and send appointment email")
    void testConsumeAppointmentConfirmation() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("patient@example.com")
                .patientName("John Doe")
                .doctorName("Dr. Smith")
                .date("2026-09-10")
                .time("10:00 AM")
                .appointmentId(101L)
                .build();

        emailNotificationConsumer.consumeAppointmentNotification(event);

        verify(emailService).sendAppointmentConfirmation(
                "patient@example.com",
                "John Doe",
                "Dr. Smith",
                "2026-09-10",
                "10:00 AM",
                101L
        );
    }
}
