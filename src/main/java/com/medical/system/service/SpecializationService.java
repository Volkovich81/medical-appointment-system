package com.medical.system.service;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.mapper.SpecializationMapper;
import com.medical.system.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medical.system.entity.Specialization;

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

    @Transactional
    public SpecializationDTO createSpecialization(SpecializationDTO specializationDTO) {
        Specialization specialization = SpecializationMapper.toEntity(specializationDTO);
        Specialization savedSpecialization = specializationRepository.save(specialization);
        return SpecializationMapper.toDto(savedSpecialization);
    }

    @Transactional
    public SpecializationDTO updateSpecialization(Long id, SpecializationDTO specializationDTO) {
        return specializationRepository.findById(id)
                .map(spec -> {
                    spec.setName(specializationDTO.getName());
                    spec.setDescription(specializationDTO.getDescription());
                    Specialization updatedSpec = specializationRepository.save(spec);
                    return SpecializationMapper.toDto(updatedSpec);
                })
                .orElse(null);
    }

    @Transactional
    public void deleteSpecialization(Long id) {
        specializationRepository.deleteById(id);
    }
}
