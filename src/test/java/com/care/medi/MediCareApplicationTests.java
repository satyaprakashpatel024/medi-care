package com.care.medi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MediCareApplicationTests {

    @Test
    @DisplayName("Should load Spring application context successfully")
    void contextLoads() {
        // Sanity check to verify context initialization
    }
}

