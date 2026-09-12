package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.dtos.OtpNotificationEvent;
import com.care.medi.dtos.PasswordChangedNotificationEvent;
import com.care.medi.utils.Constants;
import com.care.medi.utils.Helpers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Generic send method to publish any event type T to a specified topic with a key.
     */
    public <T> void sendEvent(String topic, String key, T event) {
        try {
            kafkaTemplate.send(topic, key, event).whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error(Constants.LOG_KAFKA_PRODUCE_ERROR, topic, ex.getMessage(), ex);
                } else {
                    log.info("Published Kafka event to topic '{}' with key '{}': {}", topic, Helpers.maskKey(key), event);
                }
            });
        } catch (Exception e) {
            log.error(Constants.LOG_KAFKA_PRODUCE_ERROR, topic, e.getMessage(), e);
        }
    }

    public void sendEmailNotification(EmailNotificationEvent event) {
        String key = event.getAppointmentId() != null ? event.getAppointmentId().toString() : event.getToEmail();
        sendEvent(Constants.KAFKA_TOPIC_APPOINTMENT_NOTIFICATION, key, event);
    }

    public void sendOtpNotification(String toEmail, String otp) {
        OtpNotificationEvent event = OtpNotificationEvent.builder()
                .toEmail(toEmail)
                .otp(otp)
                .build();
        sendEvent(Constants.KAFKA_TOPIC_OTP_NOTIFICATION, toEmail, event);
    }

    public void sendPasswordChangedNotification(String toEmail) {
        PasswordChangedNotificationEvent event = PasswordChangedNotificationEvent.builder()
                .toEmail(toEmail)
                .build();
        sendEvent(Constants.KAFKA_TOPIC_PASSWORD_CHANGED_NOTIFICATION, toEmail, event);
    }
}
