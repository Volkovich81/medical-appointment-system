package com.medical.system.service;

import com.medical.system.dto.AppointmentDTO;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.Patient;
import com.medical.system.entity.Doctor;
import com.medical.system.enums.AppointmentStatus;
import com.medical.system.mapper.AppointmentMapper;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.PatientRepository;
import com.medical.system.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private Appointment appointment;
    private AppointmentDTO appointmentDto;
    private Patient patient;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Иван");

        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Петр");

        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        appointmentDto = new AppointmentDTO();
        appointmentDto.setId(1L);
        appointmentDto.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointmentDto.setStatus("SCHEDULED");
        appointmentDto.setPatientId(1L);
        appointmentDto.setDoctorId(1L);
    }

    @Test
    void getAllAppointments_Success() {
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));

        List<AppointmentDTO> result = appointmentService.getAllAppointments();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getAppointmentById_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        AppointmentDTO result = appointmentService.getAppointmentById(1L);

        assertNotNull(result);
    }

    @Test
    void getAppointmentById_NotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentDTO result = appointmentService.getAppointmentById(1L);

        assertNull(result);
    }

    @Test
    void createAppointment_Success() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.createAppointment(appointmentDto);

        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void createAppointment_PatientNotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(appointmentDto);
        });
    }

    @Test
    void createAppointment_DoctorNotFound_ThrowsException() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.createAppointment(appointmentDto);
        });
    }

    @Test
    void updateAppointment_Success() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.updateAppointment(1L, appointmentDto);

        assertNotNull(result);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void updateAppointment_NotFound() {
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        AppointmentDTO result = appointmentService.updateAppointment(1L, appointmentDto);

        assertNull(result);
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void deleteAppointment_Success() {
        doNothing().when(appointmentRepository).deleteById(1L);

        appointmentService.deleteAppointment(1L);

        verify(appointmentRepository).deleteById(1L);
    }
}