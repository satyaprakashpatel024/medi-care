package com.care.medi.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class CertificateUtils {

    private CertificateUtils() {
    }

    public static String copyToTempFile(String classpathLocation) {
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);

            String extension = classpathLocation.substring(classpathLocation.lastIndexOf('.'));

            Path tempFile = Files.createTempFile("kafka-", extension);

            try (InputStream input = resource.getInputStream()) {
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            tempFile.toFile().deleteOnExit();

            return tempFile.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Unable to load certificate: " + classpathLocation, e);
        }
    }
}
