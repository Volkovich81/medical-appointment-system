package com.medical.system.mapper;

import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Patient;

public class PatientMapper {

    public static PatientDTO toDto(Patient patient) {
        if (patient == null) {
            return null;
        }

        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setFirstName(patient.getFirstName());
        dto.setLastName(patient.getLastName());
        dto.setBirthDate(patient.getBirthDate());
        dto.setPhone(patient.getPhone());
        dto.setEmail(patient.getEmail());
        return dto;
    }
}
