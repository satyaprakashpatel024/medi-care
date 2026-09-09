package com.care.medi.emails;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = mock(MimeMessage.class);
    }

    @Test
    void testSendAppointmentConfirmation_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendAppointmentConfirmation("test@example.com", "Patient", "Doctor", "2024-12-25", "10:00 AM", 1L);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendAppointmentConfirmation_Exception() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new org.springframework.mail.MailSendException("Failed")).when(mailSender).send(mimeMessage);

        emailService.sendAppointmentConfirmation("test@example.com", "Patient", "Doctor", "2024-12-25", "10:00 AM", 1L);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendAppointmentCancellation_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendAppointmentCancellation("test@example.com", "Patient", "Doctor", "2024-12-25", "10:00 AM", 1L);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendAppointmentReminder_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendAppointmentReminder("test@example.com", "Patient", "Doctor", "2024-12-25", "10:00 AM", 1L);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendAppointmentReschedule_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendAppointmentReschedule("test@example.com", "Patient", "Doctor", "2024-12-25", "10:00 AM", 1L);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendOtpEmail_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendOtpEmail("test@example.com", "123456");

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendPasswordChangedEmail_Success() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordChangedEmail("test@example.com");

        verify(mailSender).send(mimeMessage);
    }
}
