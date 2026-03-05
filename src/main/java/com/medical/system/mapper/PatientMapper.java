package com.medical.system.mapper;

import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Patient;

public class PatientMapper {
    private PatientMapper() {}

    public static PatientDTO toDto(Patient patient) {
        if (patient == null) return null;

        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());

        return dto;
    }

    public static Patient toEntity(PatientDTO dto) {
        if (dto == null) return null;

        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setBirthDate(dto.getBirthDate());
        patient.setPhone(dto.getPhone());
        patient.setEmail(dto.getEmail());

        return patient;
    }
}