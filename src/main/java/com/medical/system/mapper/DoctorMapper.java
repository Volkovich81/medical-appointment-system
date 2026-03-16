package com.medical.system.mapper;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.entity.Doctor;
import com.medical.system.entity.Specialization;
import com.medical.system.repository.SpecializationRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DoctorMapper {
    private final SpecializationRepository specializationRepository;

    public DoctorMapper(SpecializationRepository specializationRepository) {
        this.specializationRepository = specializationRepository;
    }

    public DoctorDTO toDto(Doctor doctor) {
        if (doctor == null) return null;

        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setFirstName(doctor.getFirstName());
        dto.setLastName(doctor.getLastName());
        dto.setPhone(doctor.getPhone());
        dto.setEmail(doctor.getEmail());

        if (doctor.getSpecializations() != null) {
            dto.setSpecializationIds(doctor.getSpecializations().stream()
                    .map(Specialization::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Doctor toEntity(DoctorDTO dto) {
        if (dto == null) return null;

        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setPhone(dto.getPhone());
        doctor.setEmail(dto.getEmail());

        if (dto.getSpecializationIds() != null && !dto.getSpecializationIds().isEmpty()) {
            doctor.setSpecializations(specializationRepository.findAllById(dto.getSpecializationIds()));
        }

        return doctor;
    }
}