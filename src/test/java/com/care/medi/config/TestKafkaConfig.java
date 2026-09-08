package com.care.medi.config;

import com.care.medi.dtos.EmailNotificationEvent;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@TestConfiguration
@Profile("test")
public class TestKafkaConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public ConsumerFactory<String, EmailNotificationEvent> consumerFactory() {
        return Mockito.mock(ConsumerFactory.class);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ProducerFactory<String, EmailNotificationEvent> producerFactory() {
        return Mockito.mock(ProducerFactory.class);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
}
