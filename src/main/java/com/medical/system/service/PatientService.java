package com.medical.system.service;

import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Patient;
import com.medical.system.mapper.PatientMapper;
import com.medical.system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    @Autowired
    private PatientService self;

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(PatientMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return self.getAllPatients();  // ← используем self вместо this
        }
        return patientRepository.findByLastNameIgnoreCase(lastName).stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = PatientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setFirstName(patientDTO.getFirstName());
                    patient.setLastName(patientDTO.getLastName());
                    patient.setBirthDate(patientDTO.getBirthDate());
                    patient.setPhone(patientDTO.getPhone());
                    patient.setEmail(patientDTO.getEmail());
                    Patient updatedPatient = patientRepository.save(patient);
                    return PatientMapper.toDto(updatedPatient);
                })
                .orElse(null);
    }

    @Transactional
    public boolean deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientWithAppointments(Long id) {
        Patient patient = patientRepository.findByIdWithAppointments(id);
        return PatientMapper.toDto(patient);
    }
}