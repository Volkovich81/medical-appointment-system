package com.medical.system.service;

import com.medical.system.dto.PatientDTO;
import com.medical.system.mapper.PatientMapper;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public PatientDTO getPatientById(Long id) {
        return patientRepository.findAll().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(PatientMapper::toDto)
                .orElse(null);
    }

    public List<PatientDTO> getPatientsByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return getAllPatients();
        }

        return patientRepository.findAll().stream()
                .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                .map(PatientMapper::toDto)
                .toList();
    }
}