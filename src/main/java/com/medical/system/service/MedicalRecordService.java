package com.medical.system.service;

import com.medical.system.dto.MedicalRecordDTO;
import com.medical.system.mapper.MedicalRecordMapper;
import com.medical.system.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;

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
}