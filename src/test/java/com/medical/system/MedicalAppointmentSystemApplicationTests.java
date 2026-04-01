package com.medical.system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class MedicalAppointmentSystemApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void main_Method_Coverage() {
        assertDoesNotThrow(() -> MedicalAppointmentSystemApplication.main(new String[] {}));
    }
}