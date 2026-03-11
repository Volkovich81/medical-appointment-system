package com.medical.system.service;

import com.medical.system.dto.AppointmentDTO;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.Patient;
import com.medical.system.entity.Doctor;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.PatientRepository;
import com.medical.system.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medical.system.enums.AppointmentStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

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

    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        Patient patient = patientRepository.findById(appointmentDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setStatus(AppointmentStatus.valueOf(appointmentDTO.getStatus()));
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return AppointmentMapper.toDto(savedAppointment);
    }

    @Transactional
    public AppointmentDTO updateAppointment(Long id, AppointmentDTO appointmentDTO) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
                    appointment.setStatus(AppointmentStatus.valueOf(appointmentDTO.getStatus()));
                    Appointment updatedAppointment = appointmentRepository.save(appointment);
                    return AppointmentMapper.toDto(updatedAppointment);
                })
                .orElse(null);
    }

    @Transactional
    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}