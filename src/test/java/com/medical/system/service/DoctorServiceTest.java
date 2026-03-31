package com.medical.system.service;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.entity.Doctor;
import com.medical.system.mapper.DoctorMapper;
import com.medical.system.repository.DoctorRepository;
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
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor doctor;
    private DoctorDTO doctorDto;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Петр");
        doctor.setLastName("Петров");
        doctor.setPhone("+375292345678");
        doctor.setEmail("petr@example.com");

        doctorDto = new DoctorDTO();
        doctorDto.setId(1L);
        doctorDto.setFirstName("Петр");
        doctorDto.setLastName("Петров");
        doctorDto.setPhone("+375292345678");
        doctorDto.setEmail("petr@example.com");
    }

    @Test
    void getAllDoctors_Success() {
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));
        when(doctorMapper.toDto(any(Doctor.class))).thenReturn(doctorDto);

        List<DoctorDTO> result = doctorService.getAllDoctors();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getDoctorById_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);

        DoctorDTO result = doctorService.getDoctorById(1L);

        assertNotNull(result);
        assertEquals("Петр", result.getFirstName());
    }

    @Test
    void getDoctorById_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorDTO result = doctorService.getDoctorById(1L);

        assertNull(result);
    }

    @Test
    void createDoctor_Success() {
        when(doctorMapper.toEntity(any(DoctorDTO.class))).thenReturn(doctor);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);

        DoctorDTO result = doctorService.createDoctor(doctorDto);

        assertNotNull(result);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_Success() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
        when(doctorMapper.toDto(doctor)).thenReturn(doctorDto);

        DoctorDTO result = doctorService.updateDoctor(1L, doctorDto);

        assertNotNull(result);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void updateDoctor_NotFound() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        DoctorDTO result = doctorService.updateDoctor(1L, doctorDto);

        assertNull(result);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void updateDoctor_NotFound_ReturnsNull() {
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        DoctorDTO result = doctorService.updateDoctor(999L, doctorDto);

        assertNull(result);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void deleteDoctor_Success() {
        doNothing().when(doctorRepository).deleteById(1L);

        doctorService.deleteDoctor(1L);

        verify(doctorRepository).deleteById(1L);
    }
}