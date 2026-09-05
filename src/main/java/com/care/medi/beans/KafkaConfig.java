package com.care.medi.beans;

import com.care.medi.dtos.EmailNotificationEvent;
import com.care.medi.utils.CertificateUtils;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import java.util.Map;

@Configuration
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, EmailNotificationEvent> consumerFactory(KafkaProperties properties) {

        properties.getProperties().put(
                SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                CertificateUtils.copyToTempFile("certificates/ca.pem")
        );

        properties.getProperties().put(
                SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                CertificateUtils.copyToTempFile("certificates/svc.pem")
        );

        return new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
    }

    @Bean
    public ProducerFactory<String, EmailNotificationEvent> producerFactory(
            KafkaProperties kafkaProperties) {

        Map<String, Object> props = kafkaProperties.buildProducerProperties();

        props.put(
                SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
                CertificateUtils.copyToTempFile("certificates/ca.pem"));

        props.put(
                SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG,
                CertificateUtils.copyToTempFile("certificates/svc.pem"));

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate(
            ProducerFactory<String, EmailNotificationEvent> producerFactory) {

        return new KafkaTemplate<>(producerFactory);
    }
}
