package com.medical.system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmokeTest {

    @Test
    void contextLoads() {
    }

    @Test
    void mainMethodTest() {
        MedicalAppointmentSystemApplication.main(new String[] {});
    }
}