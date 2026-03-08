package com.medical.system.service;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.mapper.SpecializationMapper;
import com.medical.system.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecializationService {
    private final SpecializationRepository specializationRepository;

    public List<SpecializationDTO> getAllSpecializations() {
        return specializationRepository.findAll().stream()
                .map(SpecializationMapper::toDto)
                .toList();
    }

    public SpecializationDTO getSpecializationById(Long id) {
        return specializationRepository.findById(id)
                .map(SpecializationMapper::toDto)
                .orElse(null);
    }
}
