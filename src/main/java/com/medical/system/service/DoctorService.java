package com.medical.system.service;

import com.medical.system.dto.DoctorDTO;
import com.medical.system.entity.Doctor;
import com.medical.system.mapper.DoctorMapper;
import com.medical.system.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    public DoctorDTO getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .map(doctorMapper::toDto)
                .orElse(null);
    }

    @Transactional
    public DoctorDTO createDoctor(DoctorDTO doctorDTO) {
        Doctor doctor = doctorMapper.toEntity(doctorDTO);
        Doctor savedDoctor = doctorRepository.save(doctor);
        return doctorMapper.toDto(savedDoctor);
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
                    return doctorMapper.toDto(updatedDoctor);
                })
                .orElse(null);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
}