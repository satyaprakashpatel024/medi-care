package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.dtos.OtpNotificationEvent;
import com.care.medi.dtos.PasswordChangedNotificationEvent;
import com.care.medi.utils.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EmailNotificationProducer emailNotificationProducer;

    @Test
    @DisplayName("Should send email notification with appointment ID key")
    void testSendEmailNotificationWithAppointmentIdKey() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("patient@example.com")
                .appointmentId(100L)
                .build();

        emailNotificationProducer.sendEmailNotification(event);

        verify(kafkaTemplate).send(eq(Constants.KAFKA_TOPIC_APPOINTMENT_NOTIFICATION), eq("100"), eq(event));
    }

    @Test
    @DisplayName("Should send email notification with email key when appointment ID is null")
    void testSendEmailNotificationWithEmailKey() {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail("patient@example.com")
                .appointmentId(null)
                .build();

        emailNotificationProducer.sendEmailNotification(event);

        verify(kafkaTemplate).send(eq(Constants.KAFKA_TOPIC_APPOINTMENT_NOTIFICATION), eq("patient@example.com"), eq(event));
    }

    @Test
    @DisplayName("Should send OTP notification")
    void testSendOtpNotification() {
        String email = "user@example.com";
        String otp = "123456";

        emailNotificationProducer.sendOtpNotification(email, otp);

        ArgumentCaptor<OtpNotificationEvent> eventCaptor = ArgumentCaptor.forClass(OtpNotificationEvent.class);
        verify(kafkaTemplate).send(eq(Constants.KAFKA_TOPIC_OTP_NOTIFICATION), eq(email), eventCaptor.capture());

        OtpNotificationEvent captured = eventCaptor.getValue();
        assertEquals(email, captured.getToEmail());
        assertEquals(otp, captured.getOtp());
    }

    @Test
    @DisplayName("Should send password changed notification")
    void testSendPasswordChangedNotification() {
        String email = "user@example.com";

        emailNotificationProducer.sendPasswordChangedNotification(email);

        ArgumentCaptor<PasswordChangedNotificationEvent> eventCaptor = ArgumentCaptor.forClass(PasswordChangedNotificationEvent.class);
        verify(kafkaTemplate).send(eq(Constants.KAFKA_TOPIC_PASSWORD_CHANGED_NOTIFICATION), eq(email), eventCaptor.capture());

        PasswordChangedNotificationEvent captured = eventCaptor.getValue();
        assertEquals(email, captured.getToEmail());
    }
}
