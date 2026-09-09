package com.care.medi.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CertificateUtilsTest {

    @Test
    @DisplayName("Should create dummy temp certificate when classpath resource does not exist")
    void testCopyToTempFileNonExistentResource() throws Exception {
        String pathString = CertificateUtils.copyToTempFile("certs/non-existent.pem");

        assertNotNull(pathString);
        File tempFile = new File(pathString);
        assertTrue(tempFile.exists());

        String content = Files.readString(Path.of(pathString));
        assertTrue(content.contains("DUMMY_CERTIFICATE"));
    }

    @Test
    @DisplayName("Should copy existing classpath resource to temp file")
    void testCopyToTempFileExistingResource() {
        // application.properties exists on classpath during tests
        String pathString = CertificateUtils.copyToTempFile("application.properties");

        assertNotNull(pathString);
        File tempFile = new File(pathString);
        assertTrue(tempFile.exists());
    }
}
