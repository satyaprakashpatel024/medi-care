package com.care.medi.services.kafka;

import com.care.medi.dtos.EmailNotificationEvent;
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
            topics = Constants.KAFKA_TOPIC,
            groupId = "email-service-group"
    )
    public void consume(EmailNotificationEvent event) {

        log.info("Received Email Event : {}", event);
        emailService.sendAppointmentConfirmation(
                event.getToEmail(),
                event.getPatientName(),
                event.getDoctorName(),
                event.getDate(),
                event.getTime(),
                event.getAppointmentId()
        );
    }
}
