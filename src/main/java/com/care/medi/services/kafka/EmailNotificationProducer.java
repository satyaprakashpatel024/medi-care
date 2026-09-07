package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailNotificationProducer {
    private static final String TOPIC = Constants.KAFKA_TOPIC;

    private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

    public void sendEmailNotification(EmailNotificationEvent event) {
        String key = event.getAppointmentId() != null ? event.getAppointmentId().toString() : event.getToEmail();
        kafkaTemplate.send(
                TOPIC,
                key,
                event
        );

        System.out.println("Event Published : " + event);
    }

    public void sendOtpNotification(String toEmail, String otp) {
        EmailNotificationEvent event = EmailNotificationEvent.builder()
                .toEmail(toEmail)
                .otp(otp)
                .eventType("FORGOT_PASSWORD_OTP")
                .build();

        kafkaTemplate.send(
                TOPIC,
                toEmail,
                event
        );

        System.out.println("OTP Event Published : " + event);
    }
}
