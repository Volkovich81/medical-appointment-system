package com.medical.system.repository;

import com.medical.system.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT DISTINCT p FROM Patient p " +
            "JOIN p.appointments a " +           // через appointments
            "JOIN a.doctor d " +                  // к доктору
            "JOIN d.specializations s " +          // к специализациям доктора
            "WHERE s.name = :specializationName")
    Page<Patient> findByDoctorSpecializationJpql(
            @Param("specializationName") String specializationName,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT p.* FROM patients p " +
            "JOIN appointments a ON p.id = a.patient_id " +
            "JOIN doctors d ON a.doctor_id = d.id " +
            "JOIN doctor_specialization ds ON d.id = ds.doctor_id " +
            "JOIN specializations s ON ds.specialization_id = s.id " +
            "WHERE s.name = :specializationName",
            countQuery = "SELECT COUNT(DISTINCT p.id) FROM patients p " +
                    "JOIN appointments a ON p.id = a.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.id " +
                    "JOIN doctor_specialization ds ON d.id = ds.doctor_id " +
                    "JOIN specializations s ON ds.specialization_id = s.id " +
                    "WHERE s.name = :specializationName",
            nativeQuery = true)
    Page<Patient> findByDoctorSpecializationNative(
            @Param("specializationName") String specializationName,
            Pageable pageable);
}