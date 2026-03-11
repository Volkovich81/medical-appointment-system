package com.medical.system.service;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.mapper.DoctorMapper;
import com.medical.system.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medical.system.entity.Doctor;

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

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = DoctorMapper.toEntity(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return DoctorMapper.toDto(savedDoctor);
    }

    @Transactional
    public DoctorDTO updateDoctor(Long id, DoctorDTO doctorDTO) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setFirstName(doctorDTO.getFirstName());
                    doctor.setLastName(doctorDTO.getLastName());
                    doctor.setPhone(doctorDTO.getPhone());
                    doctor.setEmail(doctorDTO.getEmail());
                    Doctor updatedDoctor = doctorRepository.save(doctor);
                    return DoctorMapper.toDto(updatedDoctor);
                })
                .orElse(null);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
}