package com.care.medi.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@Profile("test")
public class TestKafkaConfig {

    @Bean
    @SuppressWarnings("unchecked")
    public ConsumerFactory<String, Object> consumerFactory() {
        return Mockito.mock(ConsumerFactory.class);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public ProducerFactory<String, Object> producerFactory() {
        return Mockito.mock(ProducerFactory.class);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
}
