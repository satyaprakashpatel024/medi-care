package com.care.medi.beans;

import com.care.medi.utils.CertificateUtils;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.*;

import java.util.Map;

@Configuration
@Profile("!test")
public class KafkaConfig {

  @Bean
  public ConsumerFactory<String, Object> consumerFactory(KafkaProperties properties) {

    properties.getProperties().put(
      SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
      CertificateUtils.copyToTempFile("certificates/ca.pem")
    );

    return new DefaultKafkaConsumerFactory<>(properties.buildConsumerProperties());
  }

  @Bean
  public ProducerFactory<String, Object> producerFactory(
    KafkaProperties kafkaProperties) {

    Map<String, Object> props = kafkaProperties.buildProducerProperties();

    props.put(
      SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG,
      CertificateUtils.copyToTempFile("certificates/ca.pem"));

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate(
    ProducerFactory<String, Object> producerFactory) {

    return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  public org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
    ConsumerFactory<String, Object> consumerFactory,
    KafkaTemplate<String, Object> kafkaTemplate
  ) {
    org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<String, Object> factory =
      new org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory);

    // Exponential Backoff Retries + Dead Letter Topic (DLT) Recoverer
    org.springframework.kafka.listener.DeadLetterPublishingRecoverer recoverer =
      new org.springframework.kafka.listener.DeadLetterPublishingRecoverer(kafkaTemplate);

    org.springframework.util.backoff.ExponentialBackOff backOff =
      new org.springframework.util.backoff.ExponentialBackOff(1000L, 2.0);
    backOff.setMaxElapsedTime(10000L);

    org.springframework.kafka.listener.DefaultErrorHandler errorHandler =
      new org.springframework.kafka.listener.DefaultErrorHandler(recoverer, backOff);

    factory.setCommonErrorHandler(errorHandler);
    return factory;
  }
}
