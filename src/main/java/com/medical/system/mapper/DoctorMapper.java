package com.medical.system.mapper;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.entity.Doctor;
import com.medical.system.entity.Specialization;

import java.util.stream.Collectors;

public class DoctorMapper {
    private DoctorMapper() {}

    public static DoctorDTO toDto(Doctor doctor) {
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

    public static Doctor toEntity(DoctorDTO dto) {
        if (dto == null) return null;

        Doctor doctor = new Doctor();
        doctor.setId(dto.getId());
        doctor.setFirstName(dto.getFirstName());
        doctor.setLastName(dto.getLastName());
        doctor.setPhone(dto.getPhone());
        doctor.setEmail(dto.getEmail());

        return doctor;
    }
}