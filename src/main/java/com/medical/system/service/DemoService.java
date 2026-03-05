package com.medical.system.service;

import com.medical.system.entity.Patient;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Appointment;
import com.medical.system.repository.PatientRepository;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DemoService {
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;

    public void createWithoutTransaction(String patientName, boolean throwError) {
        Patient patient = new Patient();
        patient.setFirstName(patientName);
        patient.setLastName("Тестовый");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setEmail(patientName + "@email.com");

        Patient savedPatient = patientRepository.save(patient);

        MedicalRecord record = new MedicalRecord();
        record.setRecordDate(LocalDate.now());
        record.setDiagnosis("Диагноз");
        record.setPatient(savedPatient);

        medicalRecordRepository.save(record);

        if (throwError) {
            throw new IllegalStateException("ОШИБКА! Пациент и медкарта уже сохранены, а запись не создалась");
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus("SCHEDULED");
        appointment.setPatient(savedPatient);

        appointmentRepository.save(appointment);
    }

    @Transactional
    public void createWithTransaction(String patientName, boolean throwError) {
        Patient patient = new Patient();
        patient.setFirstName(patientName);
        patient.setLastName("Тестовый");
        patient.setBirthDate(LocalDate.of(1990, 1, 1));
        patient.setEmail(patientName + "@email.com");

        Patient savedPatient = patientRepository.save(patient);

        MedicalRecord record = new MedicalRecord();
        record.setRecordDate(LocalDate.now());
        record.setDiagnosis("Диагноз");
        record.setPatient(savedPatient);

        medicalRecordRepository.save(record);

        if (throwError) {
            throw new IllegalStateException("ОШИБКА! Но всё откатится благодаря @Transactional");
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus("SCHEDULED");
        appointment.setPatient(savedPatient);

        appointmentRepository.save(appointment);
    }
}