package com.medical.system.mapper;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.entity.MedicalRecord;

public class MedicalRecordMapper {
    private MedicalRecordMapper() {}

    public static MedicalRecordDTO toDto(MedicalRecord medicalRecord) {
        if (medicalRecord == null) return null;

        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(medicalRecord.getId());
        dto.setRecordDate(medicalRecord.getRecordDate());
        dto.setDiagnosis(medicalRecord.getDiagnosis());
        dto.setTreatment(medicalRecord.getTreatment());

        if (medicalRecord.getPatient() != null) {
            dto.setPatientId(medicalRecord.getPatient().getId());
            dto.setPatientName(medicalRecord.getPatient().getFirstName() + " " +
                    medicalRecord.getPatient().getLastName());
        }

        return dto;
    }
}