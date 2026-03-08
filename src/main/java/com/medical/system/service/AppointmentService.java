package com.medical.system.service;

import com.medical.system.dto.AppointmentDTO;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentMapper::toDto)
                .toList();
    }

    public AppointmentDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(AppointmentMapper::toDto)
                .orElse(null);
    }
}
