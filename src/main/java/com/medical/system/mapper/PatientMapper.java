package com.medical.system.mapper;

import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Patient;
import java.util.stream.Collectors;

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

        if (patient.getAppointments() != null) {
            dto.setAppointments(patient.getAppointments().stream()
                    .map(AppointmentMapper::toDto)
                    .collect(Collectors.toList()));
        }

        if (patient.getMedicalRecord() != null) {
            dto.setMedicalRecord(MedicalRecordMapper.toDto(patient.getMedicalRecord()));
        }

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