package com.medical.system.repository;

import com.medical.system.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByLastNameIgnoreCase(String lastName);

    @Query("SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.appointments WHERE p.id = :id")
    Patient findByIdWithAppointments(@Param("id") Long id);
}