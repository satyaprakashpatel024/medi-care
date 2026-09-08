package com.care.medi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class CertificateUtils {

    private static final Logger log = LoggerFactory.getLogger(CertificateUtils.class);

    private CertificateUtils() {
    }

    public static String copyToTempFile(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);

            String extension = classpathLocation.contains(".")
                    ? classpathLocation.substring(classpathLocation.lastIndexOf('.'))
                    : ".pem";

            Path tempFile = Files.createTempFile("kafka-", extension);

            if (!resource.exists()) {
                log.warn("Certificate resource '{}' not found on classpath. Creating placeholder dummy certificate file.", classpathLocation);
                Files.writeString(tempFile, "-----BEGIN CERTIFICATE-----\nDUMMY_CERTIFICATE\n-----END CERTIFICATE-----\n");
            } else {
                try (InputStream input = resource.getInputStream()) {
                    Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            tempFile.toFile().deleteOnExit();

            return tempFile.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Unable to load certificate: " + classpathLocation, e);
        }
    }
}
