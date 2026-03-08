package com.medical.system.mapper;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.entity.Specialization;

public class SpecializationMapper {
    private SpecializationMapper() {}

    public static SpecializationDTO toDto(Specialization specialization) {
        if (specialization == null) return null;

        SpecializationDTO dto = new SpecializationDTO();
        dto.setId(specialization.getId());
        dto.setName(specialization.getName());
        dto.setDescription(specialization.getDescription());

        return dto;
    }

    public static Specialization toEntity(SpecializationDTO dto) {
        if (dto == null) return null;

        Specialization specialization = new Specialization();
        specialization.setId(dto.getId());
        specialization.setName(dto.getName());
        specialization.setDescription(dto.getDescription());

        return specialization;
    }
}
