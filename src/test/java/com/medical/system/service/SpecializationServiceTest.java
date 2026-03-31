package com.medical.system.service;

import com.medical.system.dto.SpecializationDTO;
import com.medical.system.entity.Specialization;
import com.medical.system.repository.SpecializationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecializationServiceTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @InjectMocks
    private SpecializationService specializationService;

    private Specialization specialization;
    private SpecializationDTO specializationDto;

    @BeforeEach
    void setUp() {
        specialization = new Specialization();
        specialization.setId(1L);
        specialization.setName("Хирург");
        specialization.setDescription("Хирургическое отделение");

        specializationDto = new SpecializationDTO();
        specializationDto.setId(1L);
        specializationDto.setName("Хирург");
        specializationDto.setDescription("Хирургическое отделение");
    }

    @Test
    void getAllSpecializations_Success() {
        when(specializationRepository.findAll()).thenReturn(List.of(specialization));

        List<SpecializationDTO> result = specializationService.getAllSpecializations();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Хирург", result.get(0).getName());
    }

    @Test
    void getSpecializationById_Success() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));

        SpecializationDTO result = specializationService.getSpecializationById(1L);

        assertNotNull(result);
        assertEquals("Хирург", result.getName());
    }

    @Test
    void getSpecializationById_NotFound() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.empty());

        SpecializationDTO result = specializationService.getSpecializationById(1L);

        assertNull(result);
    }

    @Test
    void createSpecialization_Success() {
        when(specializationRepository.save(any(Specialization.class))).thenReturn(specialization);

        SpecializationDTO result = specializationService.createSpecialization(specializationDto);

        assertNotNull(result);
        verify(specializationRepository).save(any(Specialization.class));
    }

    @Test
    void updateSpecialization_Success() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.of(specialization));
        when(specializationRepository.save(any(Specialization.class))).thenReturn(specialization);

        SpecializationDTO result = specializationService.updateSpecialization(1L, specializationDto);

        assertNotNull(result);
        verify(specializationRepository).save(any(Specialization.class));
    }

    @Test
    void updateSpecialization_NotFound() {
        when(specializationRepository.findById(1L)).thenReturn(Optional.empty());

        SpecializationDTO result = specializationService.updateSpecialization(1L, specializationDto);

        assertNull(result);
        verify(specializationRepository, never()).save(any());
    }

    @Test
    void updateSpecialization_NotFound_ReturnsNull() {
        when(specializationRepository.findById(999L)).thenReturn(Optional.empty());

        SpecializationDTO result = specializationService.updateSpecialization(999L, specializationDto);

        assertNull(result);
        verify(specializationRepository, never()).save(any());
    }

    @Test
    void deleteSpecialization_Success() {
        doNothing().when(specializationRepository).deleteById(1L);

        specializationService.deleteSpecialization(1L);

        verify(specializationRepository).deleteById(1L);
    }
}