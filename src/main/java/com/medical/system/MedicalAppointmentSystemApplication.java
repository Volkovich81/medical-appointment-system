package com.medical.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@lombok.Generated
@SpringBootApplication
@EnableAsync
public class MedicalAppointmentSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicalAppointmentSystemApplication.class, args);
    }
}