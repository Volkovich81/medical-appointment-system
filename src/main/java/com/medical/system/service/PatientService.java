package com.medical.system.service;

import com.medical.system.cache.PatientCache;
import com.medical.system.cache.PatientSearchKey;
import com.medical.system.dto.PatientDTO;
import com.medical.system.entity.Patient;
import com.medical.system.entity.MedicalRecord;
import com.medical.system.entity.Appointment;
import com.medical.system.entity.Doctor;
import com.medical.system.enums.AppointmentStatus;
import com.medical.system.exception.BulkOperationException;
import com.medical.system.mapper.PatientMapper;
import com.medical.system.repository.PatientRepository;
import com.medical.system.repository.MedicalRecordRepository;
import com.medical.system.repository.AppointmentRepository;
import com.medical.system.repository.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientCache patientCache;

    public PatientService(PatientRepository patientRepository,
                          MedicalRecordRepository medicalRecordRepository,
                          AppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository,
                          PatientCache patientCache) {
        this.patientRepository = patientRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientCache = patientCache;
    }

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

    @Transactional(readOnly = true)
    public PatientDTO getPatientWithAppointments(Long id) {
        Patient patient = patientRepository.findByIdWithAppointments(id);
        return PatientMapper.toDto(patient);
    }

    public Page<PatientDTO> findPatientsBySpecializationCached(
            String name,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        PatientSearchKey key = new PatientSearchKey(
                name, page, size, sortBy, sortDir
        );

        if (patientCache.containsKey(key)) {
            log.info("🔵 [JPQL] ДАННЫЕ ИЗ КЭША! Ключ: {}", key);
            return patientCache.get(key);
        }

        log.info("🟡 [JPQL] ДАННЫЕ ИЗ БАЗЫ ДАННЫХ... Ключ: {}", key);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Patient> patientsPage = patientRepository
                .findByDoctorSpecializationJpql(name, pageable);

        Page<PatientDTO> dtoPage = patientsPage.map(PatientMapper::toDto);

        patientCache.put(key, dtoPage);

        return dtoPage;
    }

    public Page<PatientDTO> findPatientsBySpecializationNativeCached(
            String name,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        PatientSearchKey key = new PatientSearchKey(
                name, page, size, sortBy, sortDir
        );

        if (patientCache.containsKey(key)) {
            log.info("🔵 [NATIVE] ДАННЫЕ ИЗ КЭША! Ключ: {}", key);
            return patientCache.get(key);
        }

        log.info("🟡 [NATIVE] ДАННЫЕ ИЗ БАЗЫ ДАННЫХ... Ключ: {}", key);
        Pageable pageable = PageRequest.of(page, size);

        Page<Patient> patientsPage = patientRepository
                .findByDoctorSpecializationNative(name, pageable);

        Page<PatientDTO> dtoPage = patientsPage.map(PatientMapper::toDto);

        patientCache.put(key, dtoPage);

        return dtoPage;
    }

    private void invalidateCache() {
        patientCache.clear();
        log.info("🧹 [SERVICE] Кэш пациентов очищен (инвалидация)");
    }

    @Transactional
    public PatientDTO createPatient(PatientDTO patientDTO) {
        Patient patient = PatientMapper.toEntity(patientDTO);
        Patient savedPatient = patientRepository.save(patient);
        invalidateCache();
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
                    invalidateCache();
                    return PatientMapper.toDto(updatedPatient);
                })
                .orElse(null);
    }

    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
        invalidateCache();
    }

    public int getCacheSize() {
        return patientCache.size();
    }

    @Transactional
    public PatientDTO createWithTransaction(PatientDTO patientDTO, boolean throwError) {
        PatientDTO result = createPatientWithAppointment(patientDTO, true, throwError);
        if (!throwError) {
            invalidateCache();
        }
        return result;
    }

    public PatientDTO createWithoutTransaction(PatientDTO patientDTO, boolean throwError) {
        PatientDTO result = createPatientWithAppointment(patientDTO, false, throwError);
        if (!throwError) {
            invalidateCache();
        }
        return result;
    }

    @Transactional
    public List<PatientDTO> saveAll(List<PatientDTO> patientDtoList) {
        List<Patient> patients = patientDtoList.stream()
                .map(PatientMapper::toEntity)
                .toList();
        List<Patient> saved = patientRepository.saveAll(patients);
        invalidateCache();
        return saved.stream()
                .map(PatientMapper::toDto)
                .toList();
    }

    public List<PatientDTO> saveAllWithoutTransaction(List<PatientDTO> patientDtoList) {
        List<Patient> patients = patientDtoList.stream()
                .map(PatientMapper::toEntity)
                .toList();
        patientRepository.saveAll(patients);
        invalidateCache();
        throw new BulkOperationException("Тест: ошибка БЕЗ @Transactional");
    }

    @Transactional
    public List<PatientDTO> saveAllWithTransaction(List<PatientDTO> patientDtoList) {
        List<Patient> patients = patientDtoList.stream()
                .map(PatientMapper::toEntity)
                .toList();
        patientRepository.saveAll(patients);
        invalidateCache();
        throw new BulkOperationException("Тест: ошибка С @Transactional");
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

    private PatientDTO createPatientWithAppointment(PatientDTO patientDTO, boolean transactional, boolean throwError) {
        Patient savedPatient = createPatientWithMedicalRecord(patientDTO);
        Doctor doctor = getOrCreateDefaultDoctor();

        if (throwError) {
            String message = transactional
                    ? "ОШИБКА! Но всё откатится благодаря @Transactional"
                    : "ОШИБКА! Пациент и медкарта уже сохранены, а запись не создалась";
            throw new IllegalStateException(message);
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPatient(savedPatient);
        appointment.setDoctor(doctor);
        appointmentRepository.save(appointment);

        return PatientMapper.toDto(savedPatient);
    }
}