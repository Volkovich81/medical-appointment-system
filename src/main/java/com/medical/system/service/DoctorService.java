package com.medical.system.service;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.mapper.DoctorMapper;
import com.medical.system.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorMapper::toDto)
                .toList();
    }

    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(DoctorMapper::toDto)
                .orElse(null);
    }
}