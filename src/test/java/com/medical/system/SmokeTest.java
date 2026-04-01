package com.medical.system;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmokeTest {

    @Test
    void contextLoads() {
        Assertions.assertDoesNotThrow(() -> {
        }, "Контекст должен загружаться без ошибок");
    }

    @Test
    void mainMethodTest() {
        Assertions.assertDoesNotThrow(
                () -> MedicalAppointmentSystemApplication.main(new String[] {}),
                "Метод main должен запускаться без исключений"
        );
    }
}