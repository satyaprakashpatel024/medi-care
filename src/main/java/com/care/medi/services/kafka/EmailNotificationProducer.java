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

        kafkaTemplate.send(
                TOPIC,
                event.getAppointmentId().toString(),
                event
        );

        System.out.println("Event Published : " + event);
    }
}
