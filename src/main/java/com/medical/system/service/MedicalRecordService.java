package com.medical.system.service;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Patient;
import com.medical.system.mapper.MedicalRecordMapper;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;

    public List<MedicalRecordDTO> getAllMedicalRecords() {
        return medicalRecordRepository.findAll().stream()
                .map(MedicalRecordMapper::toDto)
                .toList();
    }

    public MedicalRecordDTO getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .map(MedicalRecordMapper::toDto)
                .orElse(null);
    }

    @Transactional
    public MedicalRecordDTO createMedicalRecord(MedicalRecordDTO medicalRecordDTO) {
        Patient patient = patientRepository.findById(medicalRecordDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException(
                        "Patient not found with id: " + medicalRecordDTO.getPatientId()));

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setRecordDate(medicalRecordDTO.getRecordDate());
        medicalRecord.setDiagnosis(medicalRecordDTO.getDiagnosis());
        medicalRecord.setTreatment(medicalRecordDTO.getTreatment());
        medicalRecord.setPatient(patient);

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return MedicalRecordMapper.toDto(savedRecord);
    }

    @Transactional
    public MedicalRecordDTO updateMedicalRecord(Long id, MedicalRecordDTO medicalRecordDTO) {
        return medicalRecordRepository.findById(id)
                .map(record -> {
                    record.setRecordDate(medicalRecordDTO.getRecordDate());
                    record.setDiagnosis(medicalRecordDTO.getDiagnosis());
                    record.setTreatment(medicalRecordDTO.getTreatment());
                    MedicalRecord updatedRecord = medicalRecordRepository.save(record);
                    return MedicalRecordMapper.toDto(updatedRecord);
                })
                .orElse(null);
    }

    @Transactional
    public void deleteMedicalRecord(Long id) {
        medicalRecordRepository.deleteById(id);
    }
}