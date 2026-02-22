package com.medical.system.repository;

import com.medical.system.entity.Patient;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Repository
public class PatientRepository {
    private final List<Patient> patients = new ArrayList<>();

    public PatientRepository() {
        patients.add(new Patient(1L, "Иван", "Петров",
                LocalDate.of(1990, 1, 1), "+375291234567", "ivan@email.com"));
        patients.add(new Patient(2L, "Мария", "Иванова",
                LocalDate.of(1985, 5, 15), "+375331234567", "maria@email.com"));
        patients.add(new Patient(3L, "Петр", "Сидоров",
                LocalDate.of(1978, 10, 20), "+375441234567", "petr@email.com"));
    }

    public List<Patient> findAll() {
        return patients;
    }
}
