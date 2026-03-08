package com.medical.system.service;

import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Patient;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.Doctor;
import com.medical.system.mapper.PatientMapper;
import com.medical.system.repository.PatientRepository;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public PatientService(PatientRepository patientRepository,
                          MedicalRecordRepository medicalRecordRepository,
                          AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(PatientMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> getPatientsByLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            return getAllPatients();
        }
        return patientRepository.findByLastNameIgnoreCase(lastName).stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = PatientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        return PatientMapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDTO updatePatient(Long id, PatientDTO patientDTO) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setFirstName(patientDTO.getFirstName());
                    patient.setLastName(patientDTO.getLastName());
                    patient.setBirthDate(patientDTO.getBirthDate());
                    patient.setPhone(patientDTO.getPhone());
                    patient.setEmail(patientDTO.getEmail());
                    Patient updatedPatient = patientRepository.save(patient);
                    return PatientMapper.toDto(updatedPatient);
                })
                .orElse(null);
    }

    @Transactional
    public boolean deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public PatientDTO getPatientWithAppointments(Long id) {
        Patient patient = patientRepository.findByIdWithAppointments(id);
        return PatientMapper.toDto(patient);
    }

    private Patient createPatientWithMedicalRecord(PatientDTO patientDTO) {
        Patient patient = PatientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setRecordDate(LocalDate.now());
        medicalRecord.setDiagnosis("Диагноз");
        medicalRecord.setPatient(savedPatient);
        medicalRecordRepository.save(medicalRecord);

        savedPatient.setMedicalRecord(medicalRecord);

        return savedPatient;
    }

    private Doctor getOrCreateDefaultDoctor() {
        return doctorRepository.findById(1L).orElseGet(() -> {
            Doctor doctor = new Doctor();
            doctor.setFirstName("Доктор");
            doctor.setLastName("По умолчанию");
            doctor.setEmail("doctor@default.com");
            return doctorRepository.save(doctor);
        });
    }

    public PatientDTO createWithoutTransaction(PatientDTO patientDTO, boolean throwError) {
        Patient savedPatient = createPatientWithMedicalRecord(patientDTO);
        Doctor doctor = getOrCreateDefaultDoctor();

        if (throwError) {
            throw new IllegalStateException("ОШИБКА! Пациент и медкарта уже сохранены, а запись не создалась");
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus("SCHEDULED");
        appointment.setPatient(savedPatient);
        appointment.setDoctor(doctor);
        appointmentRepository.save(appointment);

        return PatientMapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDTO createWithTransaction(PatientDTO patientDTO, boolean throwError) {
        Patient savedPatient = createPatientWithMedicalRecord(patientDTO);
        Doctor doctor = getOrCreateDefaultDoctor();

        if (throwError) {
            throw new IllegalStateException("ОШИБКА! Но всё откатится благодаря @Transactional");
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus("SCHEDULED");
        appointment.setPatient(savedPatient);
        appointment.setDoctor(doctor);
        appointmentRepository.save(appointment);

        return PatientMapper.toDto(savedPatient);
    }
}