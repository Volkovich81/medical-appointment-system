package com.medical.system.mapper;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.entity.MedicalRecord;

public class MedicalRecordMapper {
    private MedicalRecordMapper() {}

    public static MedicalRecordDTO toDto(MedicalRecord record) {
        if (record == null) return null;

        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setRecordDate(record.getRecordDate());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setTreatment(record.getTreatment());

        if (record.getPatient() != null) {
            dto.setPatientId(record.getPatient().getId());
            dto.setPatientName(record.getPatient().getFirstName() + " " +
                    record.getPatient().getLastName());
        }

        return dto;
    }
}
