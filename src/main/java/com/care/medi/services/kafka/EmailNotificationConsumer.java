package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.dtos.OtpNotificationEvent;
import com.care.medi.dtos.PasswordChangedNotificationEvent;
import com.care.medi.emails.EmailService;
import com.care.medi.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class EmailNotificationConsumer {

    private final EmailService emailService;

    @KafkaListener(
            topics = Constants.KAFKA_TOPIC_APPOINTMENT_NOTIFICATION,
            groupId = Constants.KAFKA_GROUP_APPOINTMENT_NOTIFICATION
    )
    public void consumeAppointmentNotification(EmailNotificationEvent event) {
        log.info("Received Appointment Notification Event : {}", event);
        try {
            emailService.sendAppointmentConfirmation(
                    event.getToEmail(),
                    event.getPatientName(),
                    event.getDoctorName(),
                    event.getDate(),
                    event.getTime(),
                    event.getAppointmentId()
            );
        } catch (Exception e) {
            log.error(Constants.LOG_KAFKA_CONSUME_ERROR, Constants.KAFKA_TOPIC_APPOINTMENT_NOTIFICATION, e);
        }
    }

    @KafkaListener(
            topics = Constants.KAFKA_TOPIC_OTP_NOTIFICATION,
            groupId = Constants.KAFKA_GROUP_OTP_NOTIFICATION
    )
    public void consumeOtpNotification(OtpNotificationEvent event) {
        log.info("Received OTP Notification Event : {}", event);
        try {
            emailService.sendOtpEmail(event.getToEmail(), event.getOtp());
        } catch (Exception e) {
            log.error(Constants.LOG_KAFKA_CONSUME_ERROR, Constants.KAFKA_TOPIC_OTP_NOTIFICATION, e);
        }
    }

    @KafkaListener(
            topics = Constants.KAFKA_TOPIC_PASSWORD_CHANGED_NOTIFICATION,
            groupId = Constants.KAFKA_GROUP_PASSWORD_CHANGED_NOTIFICATION
    )
    public void consumePasswordChangedNotification(PasswordChangedNotificationEvent event) {
        log.info("Received Password Changed Notification Event : {}", event);
        try {
            emailService.sendPasswordChangedEmail(event.getToEmail());
        } catch (Exception e) {
            log.error(Constants.LOG_KAFKA_CONSUME_ERROR, Constants.KAFKA_TOPIC_PASSWORD_CHANGED_NOTIFICATION, e);
        }
    }
}
