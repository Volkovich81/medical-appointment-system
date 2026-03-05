package com.medical.system.repository;

import com.medical.system.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByLastNameIgnoreCase(String lastName);

    @Query("SELECT d FROM Doctor d JOIN FETCH d.specializations WHERE d.id = :id")
    Doctor findByIdWithSpecializations(@Param("id") Long id);
}
